package com.releasecanvas.app.data.pdf

/**
 * Built-in release wording presets. **Not** official third-party legal forms
 * and not legal advice — inspired templates for photographers to adapt.
 */
enum class ReleaseTemplate(
    val id: String,
    val displayName: String,
    val version: String,
    val shortDescription: String,
) {
    GENERIC(
        id = "generic",
        displayName = "Generic portrait release",
        version = "GENERIC_V1",
        shortDescription = "Broad commercial / editorial / promotional grant",
    ),
    FIVE_HUNDRED_PX_STYLE(
        id = "500px_style",
        displayName = "500px-style (unofficial)",
        version = "500PX_STYLE_V1",
        shortDescription = "Community / portfolio platform–oriented grant (not an official 500px form)",
    ),
    STOCK_RF_STYLE(
        id = "stock_rf",
        displayName = "Royalty-free stock–style",
        version = "STOCK_RF_V1",
        shortDescription = "Broad commercial licensing language typical of RF stock",
    ),
    EDITORIAL_ONLY(
        id = "editorial",
        displayName = "Editorial only",
        version = "EDITORIAL_V1",
        shortDescription = "Limited to editorial / newsworthy / documentary use",
    ),
    SOCIAL_WEB(
        id = "social_web",
        displayName = "Social & web promotional",
        version = "SOCIAL_WEB_V1",
        shortDescription = "Websites, social media, and light promotional use",
    ),
    ;

    companion object {
        fun fromId(id: String?): ReleaseTemplate =
            entries.firstOrNull { it.id == id } ?: GENERIC
    }
}
