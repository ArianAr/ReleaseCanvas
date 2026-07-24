package com.releasecanvas.app.data.pdf

import android.content.Context
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicReference

/**
 * Canonical built-in template bodies loaded from assets/release_templates.json
 * (same file as shared/ and web/data/).
 */
object TemplateCatalog {
    private val cache = AtomicReference<Map<String, Map<String, String>>?>(null)

    fun ensureLoaded(context: Context) {
        if (cache.get() != null) return
        synchronized(this) {
            if (cache.get() != null) return
            val json = context.assets.open("release_templates.json").bufferedReader().use { it.readText() }
            val root = JSONObject(json)
            val arr = root.getJSONArray("templates")
            val map = mutableMapOf<String, Map<String, String>>()
            for (i in 0 until arr.length()) {
                val t = arr.getJSONObject(i)
                val id = t.getString("id")
                val bodiesObj = t.getJSONObject("bodies")
                val bodies = mutableMapOf<String, String>()
                bodiesObj.keys().forEach { lang ->
                    bodies[lang] = bodiesObj.getString(lang)
                }
                map[id] = bodies
            }
            cache.set(map)
        }
    }

    fun body(templateId: String, languageTag: String): String? {
        val all = cache.get() ?: return null
        val bodies = all[templateId] ?: return null
        return bodies[languageTag] ?: bodies["en"]
    }

    fun isLoaded(): Boolean = cache.get() != null
}
