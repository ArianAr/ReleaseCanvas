package com.releasecanvas.app.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.releasecanvas.app.data.model.HistoryEntry
import com.releasecanvas.app.data.model.PhotographerProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "release_canvas_prefs")

class PreferencesStore(private val context: Context) {

    private val shooterNameKey = stringPreferencesKey("shooter_name")
    private val historyKey = stringPreferencesKey("export_history")
    private val lastTemplateKey = stringPreferencesKey("last_release_template")
    private val profileStudioKey = stringPreferencesKey("profile_studio_name")
    private val profileEmailKey = stringPreferencesKey("profile_email")
    private val profilePhoneKey = stringPreferencesKey("profile_phone")
    private val profileLogoPathKey = stringPreferencesKey("profile_logo_path")
    private val brandingEnabledKey = booleanPreferencesKey("branding_enabled")
    private val brandAccentKey = stringPreferencesKey("brand_accent_hex")

    val shooterName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[shooterNameKey].orEmpty()
    }

    val photographerProfile: Flow<PhotographerProfile> = context.dataStore.data.map { prefs ->
        PhotographerProfile(
            displayName = prefs[shooterNameKey].orEmpty(),
            studioName = prefs[profileStudioKey].orEmpty(),
            email = prefs[profileEmailKey].orEmpty(),
            phone = prefs[profilePhoneKey].orEmpty(),
            logoPath = prefs[profileLogoPathKey].orEmpty(),
            brandingEnabled = prefs[brandingEnabledKey] ?: true,
            brandAccentHex = prefs[brandAccentKey] ?: "#3A86FF",
        )
    }

    val lastTemplateId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[lastTemplateKey].orEmpty()
    }

    val history: Flow<List<HistoryEntry>> = context.dataStore.data.map { prefs ->
        parseHistory(prefs[historyKey].orEmpty())
    }

    suspend fun setShooterName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[shooterNameKey] = name.trim()
        }
    }

    suspend fun setPhotographerProfile(profile: PhotographerProfile) {
        context.dataStore.edit { prefs ->
            prefs[shooterNameKey] = profile.displayName.trim()
            prefs[profileStudioKey] = profile.studioName.trim()
            prefs[profileEmailKey] = profile.email.trim()
            prefs[profilePhoneKey] = profile.phone.trim()
            prefs[profileLogoPathKey] = profile.logoPath.trim()
            prefs[brandingEnabledKey] = profile.brandingEnabled
            prefs[brandAccentKey] = profile.brandAccentHex.trim().ifBlank { "#3A86FF" }
        }
    }

    suspend fun setLastTemplateId(id: String) {
        context.dataStore.edit { prefs ->
            prefs[lastTemplateKey] = id
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

    /** Removes a history row only; does not delete the PDF file from storage. */
    suspend fun removeHistoryEntry(uriString: String) {
        context.dataStore.edit { prefs ->
            val current = parseHistory(prefs[historyKey].orEmpty()).filterNot { it.uriString == uriString }
            prefs[historyKey] = serializeHistory(current)
        }
    }

    /** Clears local history metadata only; PDF files remain in Documents/ReleaseCanvas. */
    suspend fun clearHistory() {
        context.dataStore.edit { prefs ->
            prefs[historyKey] = "[]"
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
