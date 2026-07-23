package com.releasecanvas.app.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.releasecanvas.app.data.model.LocationStatus
import com.releasecanvas.app.data.model.PlaceSource
import com.releasecanvas.app.data.model.SigningMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.time.Instant
import java.util.Locale
import kotlin.coroutines.resume

data class CapturedLocation(
    val latitude: Double?,
    val longitude: Double?,
    val accuracyM: Float?,
    val status: LocationStatus,
)

data class PlaceLabel(
    val city: String?,
    val country: String?,
    val source: PlaceSource,
)

class LocationRepository(private val context: Context) {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun captureForSigning(
        timeoutMs: Long = 10_000L,
        manualCity: String = "",
        manualCountry: String = "",
        clock: () -> Instant = { Instant.now() },
    ): SigningMetadata {
        val signedAt = clock()
        val manual = manualPlace(manualCity, manualCountry)

        if (!hasLocationPermission()) {
            return SigningMetadata(
                signedAtUtc = signedAt,
                latitude = null,
                longitude = null,
                locationAccuracyM = null,
                locationStatus = LocationStatus.PermissionDenied,
                placeCity = manual?.city,
                placeCountry = manual?.country,
                placeSource = manual?.source ?: PlaceSource.None,
            )
        }

        val captured = withTimeoutOrNull(timeoutMs) {
            fetchLocation()
        } ?: CapturedLocation(
            latitude = null,
            longitude = null,
            accuracyM = null,
            status = LocationStatus.Timeout,
        )

        val place = if (manual != null) {
            manual
        } else if (captured.latitude != null && captured.longitude != null) {
            reverseGeocode(captured.latitude, captured.longitude)
                ?: PlaceLabel(null, null, PlaceSource.None)
        } else {
            PlaceLabel(null, null, PlaceSource.None)
        }

        return SigningMetadata(
            signedAtUtc = signedAt,
            latitude = captured.latitude,
            longitude = captured.longitude,
            locationAccuracyM = captured.accuracyM,
            locationStatus = captured.status,
            placeCity = place.city,
            placeCountry = place.country,
            placeSource = place.source,
        )
    }

    private fun manualPlace(city: String, country: String): PlaceLabel? {
        val c = city.trim().takeIf { it.isNotEmpty() }
        val co = country.trim().takeIf { it.isNotEmpty() }
        if (c == null && co == null) return null
        return PlaceLabel(city = c, country = co, source = PlaceSource.Manual)
    }

    @SuppressLint("MissingPermission")
    private suspend fun fetchLocation(): CapturedLocation {
        val cancellation = CancellationTokenSource()
        val current: Location? = runCatching {
            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellation.token,
            ).await()
        }.getOrNull()

        val location = current ?: runCatching {
            client.lastLocation.await()
        }.getOrNull()

        return if (location != null) {
            CapturedLocation(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracyM = if (location.hasAccuracy()) location.accuracy else null,
                status = LocationStatus.Available,
            )
        } else {
            CapturedLocation(
                latitude = null,
                longitude = null,
                accuracyM = null,
                status = LocationStatus.Unavailable,
            )
        }
    }

    /**
     * Best-effort reverse geocode. Requires network + Geocoder backend;
     * returns null when offline or unsupported.
     */
    suspend fun reverseGeocode(latitude: Double, longitude: Double): PlaceLabel? =
        withContext(Dispatchers.IO) {
            if (!Geocoder.isPresent()) return@withContext null
            runCatching {
                val geocoder = Geocoder(context, Locale.getDefault())
                val address = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { cont ->
                        geocoder.getFromLocation(latitude, longitude, 1) { list ->
                            if (cont.isActive) cont.resume(list.firstOrNull())
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
                } ?: return@withContext null

                val city = sequenceOf(
                    address.locality,
                    address.subAdminArea,
                    address.adminArea,
                ).mapNotNull { it?.trim()?.takeIf(String::isNotEmpty) }.firstOrNull()

                val country = address.countryName?.trim()?.takeIf { it.isNotEmpty() }

                if (city == null && country == null) {
                    null
                } else {
                    PlaceLabel(city = city, country = country, source = PlaceSource.ReverseGeocode)
                }
            }.getOrNull()
        }
}
