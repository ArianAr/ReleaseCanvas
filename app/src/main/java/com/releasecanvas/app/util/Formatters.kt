package com.releasecanvas.app.util

import com.releasecanvas.app.data.model.LocationStatus
import com.releasecanvas.app.data.model.PlaceSource
import com.releasecanvas.app.data.model.SigningMetadata
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatters {
    private val utcDateTime: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'").withZone(ZoneOffset.UTC)

    private val fileStamp: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").withZone(ZoneOffset.UTC)

    fun formatUtc(instant: Instant): String = utcDateTime.format(instant)

    /** "City, Country" when either part is present. */
    fun formatPlaceLabel(metadata: SigningMetadata): String? {
        val city = metadata.placeCity?.trim()?.takeIf { it.isNotEmpty() }
        val country = metadata.placeCountry?.trim()?.takeIf { it.isNotEmpty() }
        return when {
            city != null && country != null -> "$city, $country"
            city != null -> city
            country != null -> country
            else -> null
        }
    }

    fun formatPlaceSource(source: PlaceSource): String? = when (source) {
        PlaceSource.Manual -> "manual"
        PlaceSource.ReverseGeocode -> "reverse geocode"
        PlaceSource.None -> null
    }

    fun formatCoordinates(metadata: SigningMetadata): String? {
        if (metadata.locationStatus != LocationStatus.Available) return null
        val lat = metadata.latitude ?: return null
        val lng = metadata.longitude ?: return null
        val accuracy = metadata.locationAccuracyM?.let {
            String.format(Locale.US, " (±%.0fm)", it)
        }.orEmpty()
        return String.format(Locale.US, "%.6f, %.6f%s", lat, lng, accuracy)
    }

    /**
     * Human-readable location summary for UI/PDF:
     * place label (if any), then coordinates or unavailable reason.
     */
    fun formatLocation(metadata: SigningMetadata): String {
        val place = formatPlaceLabel(metadata)
        val coords = formatCoordinates(metadata)
        val source = formatPlaceSource(metadata.placeSource)?.let { " [$it]" }.orEmpty()

        return when {
            place != null && coords != null -> "$place$source · $coords"
            place != null -> "$place$source"
            coords != null -> coords
            else -> when (metadata.locationStatus) {
                LocationStatus.PermissionDenied -> "unavailable (permission denied)"
                LocationStatus.Timeout -> "unavailable (timeout)"
                LocationStatus.Unavailable -> "unavailable"
                LocationStatus.Acquiring -> "acquiring"
                LocationStatus.Available -> "unavailable"
            }
        }
    }

    fun buildFileName(modelName: String, signedAt: Instant): String {
        val sanitized = sanitizeForFileName(modelName).ifBlank { "Model" }
        return "Release_${sanitized}_${fileStamp.format(signedAt)}.pdf"
    }

    fun sanitizeForFileName(input: String): String {
        return input.trim()
            .replace(Regex("[\\\\/:*?\"<>|\\s]+"), "_")
            .replace(Regex("_+"), "_")
            .take(40)
            .trim('_')
    }
}
