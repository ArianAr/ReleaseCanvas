package com.releasecanvas.app.data.model

/**
 * Saved photographer/studio defaults used to prefill new releases.
 */
data class PhotographerProfile(
    val displayName: String = "",
    val studioName: String = "",
    val email: String = "",
    val phone: String = "",
    /** Absolute path to logo file in app storage, or empty. */
    val logoPath: String = "",
)
