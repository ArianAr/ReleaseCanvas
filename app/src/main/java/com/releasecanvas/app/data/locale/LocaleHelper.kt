package com.releasecanvas.app.data.locale

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleHelper {

    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * Applies in-app UI language. Use [AppLocale.UI_FOLLOW_SYSTEM] to clear the override
     * and follow the device locale.
     *
     * Must run on the main thread; may recreate the activity when the locale changes.
     * Requires [androidx.appcompat.app.AppCompatActivity] + an AppCompat theme.
     */
    fun applyUiLanguage(uiTag: String) {
        val normalized = AppLocale.normalizeUiTag(uiTag)
        val locales = if (normalized == AppLocale.UI_FOLLOW_SYSTEM) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(normalized)
        }
        val apply = {
            AppCompatDelegate.setApplicationLocales(locales)
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            apply()
        } else {
            mainHandler.post(apply)
        }
    }
}
