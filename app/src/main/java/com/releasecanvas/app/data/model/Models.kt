package com.releasecanvas.app.data.model

import android.graphics.Bitmap
import android.net.Uri
import java.time.Instant

data class ReleaseDraft(
    val modelName: String = "",
    val modelEmail: String = "",
    val shooterName: String = "",
    val description: String = "",
    /** Optional photographer-defined shoot identifier. */
    val shootId: String = "",
    val photographerEmail: String = "",
    val photographerPhone: String = "",
    val clientAgency: String = "",
    val notes: String = "",
    /** Optional human-readable shoot location name (distinct from GPS city/country). */
    val locationName: String = "",
    /** Optional manual city (overrides reverse geocode when set). */
    val city: String = "",
    /** Optional manual country (overrides reverse geocode when set). */
    val country: String = "",
    val signatureBitmap: Bitmap? = null,
)

enum class LocationStatus {
    Available,
    PermissionDenied,
    Unavailable,
    Timeout,
    Acquiring,
}

/** How the human-readable place label was obtained. */
enum class PlaceSource {
    None,
    Manual,
    ReverseGeocode,
}

data class SigningMetadata(
    val signedAtUtc: Instant,
    val latitude: Double?,
    val longitude: Double?,
    val locationAccuracyM: Float?,
    val locationStatus: LocationStatus,
    val placeCity: String? = null,
    val placeCountry: String? = null,
    val placeSource: PlaceSource = PlaceSource.None,
)

data class ExportResult(
    val displayName: String,
    val contentUri: Uri,
    val metadata: SigningMetadata,
)

data class HistoryEntry(
    val displayName: String,
    val uriString: String,
    val signedAtUtc: String,
    val modelName: String,
)

data class FormErrors(
    val modelName: String? = null,
    val modelEmail: String? = null,
    val shooterName: String? = null,
    val description: String? = null,
) {
    val hasErrors: Boolean
        get() = modelName != null || modelEmail != null || shooterName != null || description != null
}
