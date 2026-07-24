package com.sebha.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sebha.app.R
import com.sebha.app.data.AppLanguage
import com.sebha.app.ui.theme.SebhaGold
import com.sebha.app.ui.theme.SebhaPrimary

private val CORRECTION_OPTIONS = listOf(-2, -1, 0, 1, 2)

/**
 * Material 3 settings bottom sheet.
 * Includes language selection (FR / EN / AR) and cumulative total controls.
 */
@Composable
fun SettingsPage(
    goal: Int,
    totalCount: Int,
    languageCode: String,
    vibrationEnabled: Boolean,
    soundEnabled: Boolean,
    hijriCorrectionDays: Int,
    onGoalChange: (Int) -> Unit,
    onLanguageChange: (String) -> Unit,
    onVibrationChange: (Boolean) -> Unit,
    onSoundChange: (Boolean) -> Unit,
    onHijriCorrectionChange: (Int) -> Unit,
    onRequestResetDaily: () -> Unit,
    onResetTotalCount: () -> Unit
) {
    var goalText by remember(goal) { mutableStateOf(goal.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.titleLarge,
                color = SebhaPrimary
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                LanguageChip(
                    label = stringResource(R.string.language_french),
                    selected = languageCode == AppLanguage.FRENCH,
                    onClick = { onLanguageChange(AppLanguage.FRENCH) }
                )
                LanguageChip(
                    label = stringResource(R.string.language_english),
                    selected = languageCode == AppLanguage.ENGLISH,
                    onClick = { onLanguageChange(AppLanguage.ENGLISH) }
                )
                LanguageChip(
                    label = stringResource(R.string.language_arabic),
                    selected = languageCode == AppLanguage.ARABIC,
                    onClick = { onLanguageChange(AppLanguage.ARABIC) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.goal),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = goalText,
                onValueChange = { raw ->
                    val digits = raw.filter(Char::isDigit).take(10)
                    goalText = digits
                    digits.toLongOrNull()
                        ?.takeIf { it in 1..Int.MAX_VALUE.toLong() }
                        ?.let { onGoalChange(it.toInt()) }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.cumulative_total),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = totalCount.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onResetTotalCount) {
                    Text(
                        text = stringResource(R.string.reset_cumulative_total),
                        color = SebhaGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSwitchRow(
                label = stringResource(R.string.vibration),
                checked = vibrationEnabled,
                onCheckedChange = onVibrationChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsSwitchRow(
                label = stringResource(R.string.sound),
                checked = soundEnabled,
                onCheckedChange = onSoundChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.hijri_correction),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CORRECTION_OPTIONS.forEach { days ->
                    val selected = hijriCorrectionDays == days
                    FilterChip(
                        selected = selected,
                        onClick = { onHijriCorrectionChange(days) },
                        label = {
                            Text(
                                text = when {
                                    days > 0 -> "+$days"
                                    else -> days.toString()
                                }
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SebhaPrimary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            TextButton(
                onClick = onRequestResetDaily,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text(
                    text = stringResource(R.string.reset_daily_total),
                    color = SebhaGold
                )
            }
    }

}

@Composable
private fun LanguageChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = SebhaPrimary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
