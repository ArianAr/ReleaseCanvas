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
    /** When true, logo/studio/accent appear on exported PDFs. */
    val brandingEnabled: Boolean = true,
    /** Hex RGB accent for PDF headings, e.g. #3A86FF */
    val brandAccentHex: String = "#3A86FF",
)
