package com.sebha.app.data

/**
 * Immutable snapshot of all persisted user preferences.
 *
 * @property count Current daily tasbih count (always ≥ 0).
 * @property totalCount Lifetime cumulative count (always ≥ 0).
 * @property goal Target count for the progress bar (default 33).
 * @property vibrationEnabled Soft haptic on each tap when true.
 * @property soundEnabled Soft click tone on each tap when true.
 * @property hijriCorrectionDays Manual offset applied to HijrahDate (−2…+2).
 * @property languageCode App UI language: fr, en, or ar.
 * @property hijriMonthPromptDismissedKey Key of the last Hijri month for which
 *   the "has the new month started?" dialog was answered.
 */
data class UserPreferences(
    val count: Int = 0,
    val totalCount: Int = 0,
    val goal: Int = 33,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = false,
    val hijriCorrectionDays: Int = 0,
    val languageCode: String = AppLanguage.FRENCH,
    val hijriMonthPromptDismissedKey: String = ""
)
