package com.sebha.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Single DataStore instance bound to the application context. */
private val Context.sebhaDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "sebha_preferences"
)

/**
 * Persistence layer for counter state and settings.
 * Every mutation is written immediately so nothing is lost on process death.
 */
class PreferencesRepository(private val context: Context) {

    private object Keys {
        val COUNT = intPreferencesKey("count")
        val TOTAL_COUNT = intPreferencesKey("total_count")
        val GOAL = intPreferencesKey("goal")
        val VIBRATION = booleanPreferencesKey("vibration_enabled")
        val SOUND = booleanPreferencesKey("sound_enabled")
        val HIJRI_CORRECTION = intPreferencesKey("hijri_correction_days")
        val LANGUAGE = stringPreferencesKey("language_code")
        val HIJRI_PROMPT_KEY = stringPreferencesKey("hijri_month_prompt_key")
    }

    /** Cold flow of the full preference snapshot. */
    val preferences: Flow<UserPreferences> = context.sebhaDataStore.data.map { prefs ->
        val dailyCount = prefs[Keys.COUNT] ?: 0
        UserPreferences(
            count = dailyCount,
            // Existing installations start the lifetime total from their saved count.
            totalCount = prefs[Keys.TOTAL_COUNT] ?: dailyCount,
            goal = prefs[Keys.GOAL] ?: 33,
            vibrationEnabled = prefs[Keys.VIBRATION] ?: true,
            soundEnabled = prefs[Keys.SOUND] ?: false,
            hijriCorrectionDays = (prefs[Keys.HIJRI_CORRECTION] ?: 0).coerceIn(-2, 2),
            languageCode = AppLanguage.normalize(prefs[Keys.LANGUAGE]),
            hijriMonthPromptDismissedKey = prefs[Keys.HIJRI_PROMPT_KEY] ?: ""
        )
    }

    /** Atomically increments both counters, including during very fast taps. */
    suspend fun incrementCounters() {
        context.sebhaDataStore.edit { prefs ->
            val dailyCount = prefs[Keys.COUNT] ?: 0
            val totalCount = prefs[Keys.TOTAL_COUNT] ?: dailyCount
            prefs[Keys.COUNT] = dailyCount + 1
            prefs[Keys.TOTAL_COUNT] = totalCount + 1
        }
    }

    suspend fun setGoal(value: Int) {
        context.sebhaDataStore.edit { it[Keys.GOAL] = value.coerceAtLeast(1) }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.sebhaDataStore.edit { it[Keys.VIBRATION] = enabled }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.sebhaDataStore.edit { it[Keys.SOUND] = enabled }
    }

    suspend fun setHijriCorrection(days: Int) {
        context.sebhaDataStore.edit { it[Keys.HIJRI_CORRECTION] = days.coerceIn(-2, 2) }
    }

    suspend fun setLanguage(code: String) {
        context.sebhaDataStore.edit { it[Keys.LANGUAGE] = AppLanguage.normalize(code) }
    }

    suspend fun setHijriMonthPromptKey(key: String) {
        context.sebhaDataStore.edit { it[Keys.HIJRI_PROMPT_KEY] = key }
    }

    suspend fun resetDailyCount() {
        context.sebhaDataStore.edit { it[Keys.COUNT] = 0 }
    }

    suspend fun resetTotalCount() {
        context.sebhaDataStore.edit { it[Keys.TOTAL_COUNT] = 0 }
    }
}
