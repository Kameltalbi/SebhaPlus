package com.sebha.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebha.app.ui.SebhaScreen
import com.sebha.app.ui.SebhaViewModel
import com.sebha.app.ui.SebhaViewModelFactory
import com.sebha.app.ui.theme.SebhaTheme
import com.sebha.app.util.LocaleHelper

/**
 * Single-activity host for the Sebha counter screen.
 * Edge-to-edge + Material 3 theming; all state lives in [SebhaViewModel].
 */
class MainActivity : ComponentActivity() {

    private val viewModel: SebhaViewModel by viewModels {
        SebhaViewModelFactory(application)
    }

    /** Language the current activity instance was created with. */
    private var appliedLanguage: String = ""

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appliedLanguage = LocaleHelper.persistedLanguage(this)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val darkTheme = isSystemInDarkTheme()

            // Recreate the activity once when the chosen language changes.
            LaunchedEffect(uiState.isLoaded, uiState.languageCode) {
                if (uiState.isLoaded && uiState.languageCode != appliedLanguage) {
                    LocaleHelper.persist(this@MainActivity, uiState.languageCode)
                    recreate()
                }
            }

            SebhaTheme(darkTheme = darkTheme) {
                SebhaScreen(
                    state = uiState,
                    onTap = viewModel::onTap,
                    onOpenSettings = viewModel::openSettings,
                    onCloseSettings = viewModel::closeSettings,
                    onGoalChange = viewModel::setGoal,
                    onLanguageChange = viewModel::setLanguage,
                    onVibrationChange = viewModel::setVibrationEnabled,
                    onSoundChange = viewModel::setSoundEnabled,
                    onHijriCorrectionChange = viewModel::setHijriCorrection,
                    onRequestResetDaily = viewModel::requestResetConfirm,
                    onRequestResetTotal = viewModel::requestResetTotalConfirm,
                    onResetDailyCount = viewModel::resetDailyCount,
                    onResetTotalCount = viewModel::resetTotalCount,
                    onDismissReset = viewModel::dismissResetConfirm,
                    onConfirmNewHijriMonth = viewModel::confirmNewHijriMonth,
                    onDismissNewHijriMonth = viewModel::dismissNewHijriMonthPrompt
                )
            }
        }
    }
}
