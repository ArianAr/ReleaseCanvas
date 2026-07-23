package com.releasecanvas.app.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.releasecanvas.app.data.model.HistoryEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "release_canvas_prefs")

class PreferencesStore(private val context: Context) {

    private val shooterNameKey = stringPreferencesKey("shooter_name")
    private val historyKey = stringPreferencesKey("export_history")

    val shooterName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[shooterNameKey].orEmpty()
    }

    val history: Flow<List<HistoryEntry>> = context.dataStore.data.map { prefs ->
        parseHistory(prefs[historyKey].orEmpty())
    }

    suspend fun setShooterName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[shooterNameKey] = name.trim()
        }
    }

    suspend fun addHistoryEntry(entry: HistoryEntry, maxEntries: Int = 50) {
        context.dataStore.edit { prefs ->
            val current = parseHistory(prefs[historyKey].orEmpty()).toMutableList()
            current.removeAll { it.uriString == entry.uriString }
            current.add(0, entry)
            prefs[historyKey] = serializeHistory(current.take(maxEntries))
        }
    }

    private fun parseHistory(raw: String): List<HistoryEntry> {
        if (raw.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(
                        HistoryEntry(
                            displayName = obj.optString("displayName"),
                            uriString = obj.optString("uriString"),
                            signedAtUtc = obj.optString("signedAtUtc"),
                            modelName = obj.optString("modelName"),
                        ),
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun serializeHistory(entries: List<HistoryEntry>): String {
        val array = JSONArray()
        entries.forEach { entry ->
            array.put(
                JSONObject()
                    .put("displayName", entry.displayName)
                    .put("uriString", entry.uriString)
                    .put("signedAtUtc", entry.signedAtUtc)
                    .put("modelName", entry.modelName),
            )
        }
        return array.toString()
    }
}
