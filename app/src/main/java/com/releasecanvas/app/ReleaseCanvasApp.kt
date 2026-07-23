package com.releasecanvas.app

import android.app.Application
import com.releasecanvas.app.data.locale.AppLocale
import com.releasecanvas.app.data.locale.LocaleHelper
import com.releasecanvas.app.data.prefs.PreferencesStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ReleaseCanvasApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Apply stored UI language before first Activity draws chrome.
        runBlocking {
            val tag = PreferencesStore(this@ReleaseCanvasApp).uiLanguageTag.first()
            LocaleHelper.applyUiLanguage(AppLocale.normalizeUiTag(tag))
        }
    }
}
