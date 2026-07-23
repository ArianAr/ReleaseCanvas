package com.releasecanvas.app.data.locale

/**
 * Supported language tags for UI and release/template channels.
 * UI may also use [UI_FOLLOW_SYSTEM].
 */
object AppLocale {
    const val UI_FOLLOW_SYSTEM = "system"

    /** Codes available for both UI (plus system) and release language. */
    val SUPPORTED_TAGS: List<String> = listOf("en", "es", "fr", "it", "de", "fa")

    fun normalizeUiTag(raw: String?): String {
        val tag = raw?.trim().orEmpty()
        if (tag.isEmpty() || tag == UI_FOLLOW_SYSTEM) return UI_FOLLOW_SYSTEM
        return if (tag in SUPPORTED_TAGS) tag else UI_FOLLOW_SYSTEM
    }

    fun normalizeReleaseTag(raw: String?): String {
        val tag = raw?.trim().orEmpty().ifEmpty { "en" }
        return if (tag in SUPPORTED_TAGS) tag else "en"
    }

    fun isRtl(tag: String): Boolean = normalizeReleaseTag(tag) == "fa"

    /** Stable native display names (not tied to current UI locale). */
    fun displayName(tag: String): String = when (normalizeReleaseTag(tag)) {
        "en" -> "English"
        "es" -> "Español"
        "fr" -> "Français"
        "it" -> "Italiano"
        "de" -> "Deutsch"
        "fa" -> "فارسی"
        else -> tag
    }
}
