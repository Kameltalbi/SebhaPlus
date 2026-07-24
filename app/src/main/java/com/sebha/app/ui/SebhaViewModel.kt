package com.sebha.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sebha.app.data.AppLanguage
import com.sebha.app.data.PreferencesRepository
import com.sebha.app.data.UserPreferences
import com.sebha.app.util.HapticHelper
import com.sebha.app.util.HijriDateHelper
import com.sebha.app.util.LocaleHelper
import com.sebha.app.util.SoundHelper
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI-facing immutable state for the single Sebha screen.
 */
data class SebhaUiState(
    val count: Int = 0,
    val totalCount: Int = 0,
    val goal: Int = 33,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = false,
    val hijriCorrectionDays: Int = 0,
    val languageCode: String = "fr",
    val gregorianDate: String = "",
    val hijriDate: String = "",
    val showSettings: Boolean = false,
    val showHijriMonthPrompt: Boolean = false,
    val showResetConfirm: Boolean = false,
    val isLoaded: Boolean = false
)

/**
 * MVVM ViewModel: owns counter logic, feedback, and Hijri calendar prompts.
 */
class SebhaViewModel(
    application: Application,
    private val repository: PreferencesRepository = PreferencesRepository(application),
    private val haptics: HapticHelper = HapticHelper(application),
    private val sound: SoundHelper = SoundHelper()
) : AndroidViewModel(application) {

    private val sheetState = MutableStateFlow(SheetFlags())

    val uiState: StateFlow<SebhaUiState> = combine(
        repository.preferences,
        sheetState
    ) { prefs, sheets ->
        buildUiState(prefs, sheets)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SebhaUiState()
    )

    /** Increments the counter by one and persists immediately. */
    fun onTap() {
        val current = uiState.value
        if (!current.isLoaded) return

        val newCount = current.count + 1
        viewModelScope.launch { repository.incrementCounters() }

        if (current.vibrationEnabled) {
            when {
                newCount == current.goal -> haptics.goalReached()
                newCount == 33 || newCount == 99 || newCount == 100 -> haptics.milestone()
                else -> haptics.tick()
            }
        }
        if (current.soundEnabled) {
            sound.playClick()
        }
    }

    fun openSettings() = sheetState.update { it.copy(showSettings = true) }

    fun closeSettings() = sheetState.update {
        it.copy(showSettings = false, showResetConfirm = false)
    }

    fun setGoal(goal: Int) {
        viewModelScope.launch { repository.setGoal(goal) }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setVibrationEnabled(enabled) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setSoundEnabled(enabled) }
    }

    fun setHijriCorrection(days: Int) {
        viewModelScope.launch { repository.setHijriCorrection(days) }
    }

    fun setLanguage(code: String) {
        val normalized = AppLanguage.normalize(code)
        if (normalized == uiState.value.languageCode) return
        // Mirror synchronously so attachBaseContext picks it up on recreate.
        LocaleHelper.persist(getApplication(), normalized)
        viewModelScope.launch {
            repository.setLanguage(normalized)
        }
    }

    fun requestResetConfirm() = sheetState.update { it.copy(showResetConfirm = true) }

    fun dismissResetConfirm() = sheetState.update { it.copy(showResetConfirm = false) }

    fun resetDailyCount() {
        viewModelScope.launch {
            repository.resetDailyCount()
            sheetState.update { it.copy(showResetConfirm = false) }
        }
    }

    fun resetTotalCount() {
        viewModelScope.launch {
            repository.resetTotalCount()
            sheetState.update { it.copy(showResetConfirm = false) }
        }
    }

    /**
     * User confirmed the new Hijri month started on day 29.
     * Bumps the Hijri correction by +1 (capped at +2) and remembers the answer.
     */
    fun confirmNewHijriMonth() {
        val state = uiState.value
        val date = HijriDateHelper.today(state.hijriCorrectionDays)
        val key = HijriDateHelper.monthKey(date)
        viewModelScope.launch {
            repository.setHijriMonthPromptKey(key)
            val bumped = (state.hijriCorrectionDays + 1).coerceAtMost(2)
            repository.setHijriCorrection(bumped)
        }
    }

    /** User said the new month has not started — remember so we don't ask again. */
    fun dismissNewHijriMonthPrompt() {
        val state = uiState.value
        val date = HijriDateHelper.today(state.hijriCorrectionDays)
        val key = HijriDateHelper.monthKey(date)
        viewModelScope.launch {
            repository.setHijriMonthPromptKey(key)
        }
    }

    override fun onCleared() {
        sound.release()
        super.onCleared()
    }

    private fun buildUiState(prefs: UserPreferences, sheets: SheetFlags): SebhaUiState {
        val locale = Locale.forLanguageTag(prefs.languageCode)
        val hijri = HijriDateHelper.today(prefs.hijriCorrectionDays)
        val shouldPrompt = HijriDateHelper.shouldPromptNewMonth(
            date = hijri,
            dismissedKey = prefs.hijriMonthPromptDismissedKey
        )

        return SebhaUiState(
            count = prefs.count,
            totalCount = prefs.totalCount,
            goal = prefs.goal,
            vibrationEnabled = prefs.vibrationEnabled,
            soundEnabled = prefs.soundEnabled,
            hijriCorrectionDays = prefs.hijriCorrectionDays,
            languageCode = prefs.languageCode,
            gregorianDate = HijriDateHelper.formatGregorian(locale),
            hijriDate = HijriDateHelper.formatLong(hijri, prefs.languageCode, prefs.hijriCorrectionDays),
            showSettings = sheets.showSettings,
            showHijriMonthPrompt = shouldPrompt,
            showResetConfirm = sheets.showResetConfirm,
            isLoaded = true
        )
    }

    private data class SheetFlags(
        val showSettings: Boolean = false,
        val showResetConfirm: Boolean = false
    )
}

/** Simple factory so MainActivity can construct the ViewModel without a DI framework. */
class SebhaViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SebhaViewModel::class.java)) {
            return SebhaViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
