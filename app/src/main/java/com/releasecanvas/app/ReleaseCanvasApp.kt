package com.releasecanvas.app

import android.app.Application
import com.releasecanvas.app.data.locale.AppLocale
import com.releasecanvas.app.data.locale.LocaleHelper
import com.releasecanvas.app.data.pdf.TemplateCatalog
import com.releasecanvas.app.data.prefs.PreferencesStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ReleaseCanvasApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Load shared template catalog (same JSON as web/shared).
        TemplateCatalog.ensureLoaded(this)
        // Apply stored UI language before first Activity draws chrome.
        // Kept brief; DataStore first() is local.
        runBlocking {
            val tag = PreferencesStore(this@ReleaseCanvasApp).uiLanguageTag.first()
            LocaleHelper.applyUiLanguage(AppLocale.normalizeUiTag(tag))
        }
        // Warm nothing else on main
        appScope.launch { /* reserved */ }
    }
}
