package com.sebha.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebha.app.R
import com.sebha.app.data.AppLanguage
import com.sebha.app.ui.theme.SebhaCream
import com.sebha.app.ui.theme.SebhaCreamDark
import com.sebha.app.ui.theme.SebhaGold
import com.sebha.app.ui.theme.SebhaPrimary
import com.sebha.app.ui.theme.SebhaProgressTrack
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.min

private val CORRECTION_OPTIONS = listOf(-2, -1, 0, 1, 2)
private val CardRadius = 20.dp
private val CardPadding = 20.dp
private val SectionGap = 18.dp

/**
 * Full-page settings with premium Material 3 cards.
 * Functionality unchanged — UI/UX only.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    dailyCount: Int,
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
    onRequestResetTotal: () -> Unit
) {
    var goalText by remember(goal) { mutableStateOf(goal.toString()) }
    val progress = if (goal > 0) min(dailyCount.toFloat() / goal.toFloat(), 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 450),
        label = "dailyProgress"
    )
    val percent = if (goal > 0) ((dailyCount * 100) / goal).coerceAtMost(100) else 0
    val cream = if (isSystemInDarkTheme()) SebhaCreamDark else SebhaCream

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 28.dp)
    ) {
        // Compact TopAppBar-style header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null,
                tint = SebhaPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.settings),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = SebhaPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 1. Preferences
        SettingsCard {
            SectionTitle(stringResource(R.string.settings_preferences_title))
            Spacer(modifier = Modifier.height(16.dp))

            SettingsItemHeader(
                icon = Icons.Outlined.Translate,
                title = stringResource(R.string.language),
                subtitle = stringResource(R.string.settings_language_subtitle)
            )
            Spacer(modifier = Modifier.height(12.dp))

            val languages = listOf(
                AppLanguage.FRENCH to stringResource(R.string.language_french),
                AppLanguage.ENGLISH to stringResource(R.string.language_english),
                AppLanguage.ARABIC to stringResource(R.string.language_arabic)
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                languages.forEachIndexed { index, (code, label) ->
                    SegmentedButton(
                        selected = languageCode == code,
                        onClick = { onLanguageChange(code) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = languages.size
                        ),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = SebhaPrimary,
                            activeContentColor = MaterialTheme.colorScheme.onPrimary,
                            inactiveContainerColor = MaterialTheme.colorScheme.surface,
                            inactiveContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = label,
                            maxLines = 1,
                            fontSize = 12.sp,
                            fontWeight = if (languageCode == code) FontWeight.SemiBold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SettingsItemHeader(
                icon = Icons.Outlined.TrackChanges,
                title = stringResource(R.string.settings_daily_goal),
                subtitle = stringResource(R.string.settings_daily_goal_subtitle)
            )
            Spacer(modifier = Modifier.height(12.dp))
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
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SebhaPrimary,
                    cursorColor = SebhaPrimary,
                    focusedLabelColor = SebhaPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(SectionGap))

        // 2. Notifications
        SettingsCard {
            SectionTitle(stringResource(R.string.settings_notifications_title))
            Spacer(modifier = Modifier.height(8.dp))

            SettingsSwitchRow(
                icon = Icons.Outlined.Vibration,
                title = stringResource(R.string.vibration),
                subtitle = stringResource(R.string.settings_vibration_subtitle),
                checked = vibrationEnabled,
                onCheckedChange = onVibrationChange
            )
            SettingsSwitchRow(
                icon = Icons.Outlined.VolumeUp,
                title = stringResource(R.string.sound),
                subtitle = stringResource(R.string.settings_sound_subtitle),
                checked = soundEnabled,
                onCheckedChange = onSoundChange
            )
        }

        Spacer(modifier = Modifier.height(SectionGap))

        // 3. Hijri calendar
        SettingsCard {
            SectionTitle(stringResource(R.string.settings_hijri_title))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.settings_hijri_subtitle),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                CORRECTION_OPTIONS.forEachIndexed { index, days ->
                    val selected = hijriCorrectionDays == days
                    val label = when {
                        days > 0 -> "+$days"
                        else -> days.toString()
                    }
                    SegmentedButton(
                        selected = selected,
                        onClick = { onHijriCorrectionChange(days) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = CORRECTION_OPTIONS.size
                        ),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = SebhaPrimary,
                            activeContentColor = MaterialTheme.colorScheme.onPrimary,
                            inactiveContainerColor = MaterialTheme.colorScheme.surface,
                            inactiveContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        icon = {}
                    ) {
                        Text(
                            text = label,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(SectionGap))

        // 4. Statistics (daily + total merged)
        SettingsCard {
            SectionTitle(stringResource(R.string.settings_stats_title))
            Spacer(modifier = Modifier.height(16.dp))

            SettingsItemHeader(
                icon = Icons.Outlined.Flag,
                title = stringResource(R.string.settings_daily_goal),
                subtitle = null
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${formatCount(dailyCount)} / ${formatCount(goal)}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SebhaPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = SebhaPrimary,
                trackColor = SebhaProgressTrack,
                strokeCap = StrokeCap.Round,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$percent%",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedButton(
                onClick = onRequestResetDaily,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.settings_reset_today))
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
            Spacer(modifier = Modifier.height(18.dp))

            SettingsItemHeader(
                icon = Icons.Rounded.BarChart,
                title = stringResource(R.string.cumulative_total),
                subtitle = null
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatCount(totalCount),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = SebhaPrimary,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedButton(
                onClick = onRequestResetTotal,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.RestartAlt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.settings_reset_total))
            }
        }

        Spacer(modifier = Modifier.height(SectionGap))

        // Spiritual message
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CardRadius),
            colors = CardDefaults.cardColors(containerColor = cream),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.DarkMode,
                    contentDescription = null,
                    tint = SebhaGold,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.settings_footer_dua),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = SebhaPrimary,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(CardPadding)) {
            content()
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = SebhaPrimary
    )
}

@Composable
private fun SettingsItemHeader(
    icon: ImageVector,
    title: String,
    subtitle: String?
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SebhaPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SebhaPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = SebhaPrimary,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            )
        )
    }
}

private fun formatCount(value: Int): String =
    NumberFormat.getIntegerInstance(Locale.getDefault()).format(value)
