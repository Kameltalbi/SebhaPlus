package com.sebha.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sebha.app.R
import com.sebha.app.ui.components.BottomDestination
import com.sebha.app.ui.components.DateHeader
import com.sebha.app.ui.components.HijriCalendarPage
import com.sebha.app.ui.components.HolidaysPage
import com.sebha.app.ui.components.ProgressSection
import com.sebha.app.ui.components.SebhaFooter
import com.sebha.app.ui.components.SettingsPage
import com.sebha.app.ui.components.TapButton

/**
 * Single-screen Sebha experience with a 4-item bottom menu.
 */
@Composable
fun SebhaScreen(
    state: SebhaUiState,
    onTap: () -> Unit,
    onOpenSettings: () -> Unit,
    onCloseSettings: () -> Unit,
    onGoalChange: (Int) -> Unit,
    onLanguageChange: (String) -> Unit,
    onVibrationChange: (Boolean) -> Unit,
    onSoundChange: (Boolean) -> Unit,
    onHijriCorrectionChange: (Int) -> Unit,
    onRequestResetDaily: () -> Unit,
    onRequestResetTotal: () -> Unit,
    onResetDailyCount: () -> Unit,
    onResetTotalCount: () -> Unit,
    onDismissReset: () -> Unit,
    onConfirmNewHijriMonth: () -> Unit,
    onDismissNewHijriMonth: () -> Unit
) {
    val screenTapInteraction = remember { MutableInteractionSource() }
    var selectedDestination by remember { mutableStateOf(BottomDestination.SEBHA) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                when (selectedDestination) {
                    BottomDestination.SEBHA -> {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .statusBarsPadding()
                                    .padding(horizontal = 24.dp)
                                    .padding(top = 16.dp)
                                    .semantics { role = Role.Button }
                                    .clickable(
                                        interactionSource = screenTapInteraction,
                                        indication = null,
                                        onClickLabel = stringResource(R.string.tap_button_content_desc),
                                        onClick = onTap
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                DateHeader(
                                    gregorianDate = state.gregorianDate,
                                    hijriDate = state.hijriDate,
                                    onSettingsClick = {
                                        onOpenSettings()
                                        selectedDestination = BottomDestination.SETTINGS
                                    }
                                )

                                Spacer(modifier = Modifier.weight(0.7f))

                                TapButton(
                                    count = state.count,
                                    onClick = onTap
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                ProgressSection(
                                    count = state.count,
                                    goal = state.goal
                                )

                                Spacer(modifier = Modifier.height(18.dp))
                            }

                            SmallFloatingActionButton(
                                onClick = onRequestResetDaily,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 16.dp, bottom = 16.dp),
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = stringResource(R.string.reset_daily_total)
                                )
                            }
                        }
                    }

                    BottomDestination.HIJRI_CALENDAR -> {
                        HijriCalendarPage(
                            hijriCorrectionDays = state.hijriCorrectionDays
                        )
                    }

                    BottomDestination.HOLIDAYS -> {
                        HolidaysPage(
                            hijriCorrectionDays = state.hijriCorrectionDays
                        )
                    }

                    BottomDestination.SETTINGS -> {
                        SettingsPage(
                            dailyCount = state.count,
                            goal = state.goal,
                            totalCount = state.totalCount,
                            languageCode = state.languageCode,
                            vibrationEnabled = state.vibrationEnabled,
                            soundEnabled = state.soundEnabled,
                            hijriCorrectionDays = state.hijriCorrectionDays,
                            onGoalChange = onGoalChange,
                            onLanguageChange = onLanguageChange,
                            onVibrationChange = onVibrationChange,
                            onSoundChange = onSoundChange,
                            onHijriCorrectionChange = onHijriCorrectionChange,
                            onRequestResetDaily = onRequestResetDaily,
                            onRequestResetTotal = onRequestResetTotal
                        )
                    }
                }
            }

            SebhaFooter(
                selected = selectedDestination,
                onDestinationSelected = { destination ->
                    if (
                        selectedDestination == BottomDestination.SETTINGS &&
                        destination != BottomDestination.SETTINGS
                    ) {
                        onCloseSettings()
                    }
                    if (
                        destination == BottomDestination.SETTINGS &&
                        selectedDestination != BottomDestination.SETTINGS
                    ) {
                        onOpenSettings()
                    }
                    selectedDestination = destination
                },
                modifier = Modifier.navigationBarsPadding()
            )
        }

        if (state.showResetConfirm) {
            AlertDialog(
                onDismissRequest = onDismissReset,
                title = { Text(stringResource(R.string.reset_daily_confirm_title)) },
                text = { Text(stringResource(R.string.reset_daily_confirm_message)) },
                confirmButton = {
                    TextButton(onClick = onResetDailyCount) {
                        Text(stringResource(R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissReset) {
                        Text(stringResource(R.string.cancel))
                    }
                },
                shape = RoundedCornerShape(28.dp)
            )
        }

        if (state.showResetTotalConfirm) {
            AlertDialog(
                onDismissRequest = onDismissReset,
                title = { Text(stringResource(R.string.reset_total_confirm_title)) },
                text = { Text(stringResource(R.string.reset_total_confirm_message)) },
                confirmButton = {
                    TextButton(onClick = onResetTotalCount) {
                        Text(stringResource(R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissReset) {
                        Text(stringResource(R.string.cancel))
                    }
                },
                shape = RoundedCornerShape(28.dp)
            )
        }

        if (state.showHijriMonthPrompt) {
            AlertDialog(
                onDismissRequest = onDismissNewHijriMonth,
                title = { Text(stringResource(R.string.hijri_new_month_title)) },
                text = { Text(stringResource(R.string.hijri_new_month_message)) },
                confirmButton = {
                    TextButton(onClick = onConfirmNewHijriMonth) {
                        Text(stringResource(R.string.yes))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissNewHijriMonth) {
                        Text(stringResource(R.string.no))
                    }
                },
                shape = RoundedCornerShape(28.dp)
            )
        }

    }
}
