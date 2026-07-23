package com.releasecanvas.app.data.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleHelper {

    /**
     * Applies in-app UI language. Use [AppLocale.UI_FOLLOW_SYSTEM] to clear the override
     * and follow the device locale.
     */
    fun applyUiLanguage(uiTag: String) {
        val normalized = AppLocale.normalizeUiTag(uiTag)
        val locales = if (normalized == AppLocale.UI_FOLLOW_SYSTEM) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(normalized)
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
