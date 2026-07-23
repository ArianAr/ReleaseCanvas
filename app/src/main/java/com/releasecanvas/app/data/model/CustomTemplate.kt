package com.releasecanvas.app.data.model

/**
 * User-imported release wording. Placeholders:
 * `{{MODEL}}`, `{{PHOTOGRAPHER}}` (case-sensitive).
 */
data class CustomTemplate(
    val id: String,
    val name: String,
    val version: String,
    val body: String,
    /** BCP-47 tag for future i18n; body is used as authored. */
    val languageTag: String = "und",
    /** Free-text jurisdiction label (e.g. "US generic", "EU-style") — not legal certification. */
    val jurisdiction: String = "",
)

/**
 * Unified entry for the form picker (built-in or custom).
 */
data class TemplateOption(
    val id: String,
    val displayName: String,
    val version: String,
    val shortDescription: String,
    val isCustom: Boolean,
)
