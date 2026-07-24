package com.sebha.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebha.app.R
import com.sebha.app.data.AppLanguage
import com.sebha.app.ui.theme.SebhaGold
import com.sebha.app.ui.theme.SebhaPrimary
import com.sebha.app.ui.theme.SebhaProgressTrack
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.min

private val CORRECTION_OPTIONS = listOf(-2, -1, 0, 1, 2)

/**
 * Full-page settings with card sections matching the premium Sebha+ design.
 */
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
            color = SebhaPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.settings_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        SettingsSectionCard(
            icon = Icons.Outlined.Settings,
            title = stringResource(R.string.settings_general_title),
            subtitle = stringResource(R.string.settings_general_subtitle)
        ) {
            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                LanguageChip(
                    label = stringResource(R.string.language_french),
                    selected = languageCode == AppLanguage.FRENCH,
                    onClick = { onLanguageChange(AppLanguage.FRENCH) },
                    modifier = Modifier.weight(1f)
                )
                LanguageChip(
                    label = stringResource(R.string.language_english),
                    selected = languageCode == AppLanguage.ENGLISH,
                    onClick = { onLanguageChange(AppLanguage.ENGLISH) },
                    modifier = Modifier.weight(1f)
                )
                LanguageChip(
                    label = stringResource(R.string.language_arabic),
                    selected = languageCode == AppLanguage.ARABIC,
                    onClick = { onLanguageChange(AppLanguage.ARABIC) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.settings_daily_goal),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSectionCard(
            icon = Icons.Outlined.TouchApp,
            title = stringResource(R.string.settings_daily_progress_title),
            subtitle = stringResource(R.string.settings_daily_progress_subtitle)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.settings_today_label))
                        append(" ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = SebhaPrimary)) {
                            append(formatCount(dailyCount))
                        }
                        append(" ")
                        append(stringResource(R.string.settings_of_label))
                        append(" ")
                        append(formatCount(goal))
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                DailyProgressRing(
                    progress = if (goal > 0) min(dailyCount.toFloat() / goal.toFloat(), 1f) else 0f,
                    percent = if (goal > 0) ((dailyCount * 100) / goal).coerceAtMost(100) else 0
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onRequestResetDaily)
                    .background(
                        color = SebhaPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                        tint = SebhaPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.settings_reset_today),
                        color = SebhaPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSectionCard(
            icon = Icons.Outlined.BarChart,
            title = stringResource(R.string.settings_total_stats_title),
            subtitle = stringResource(R.string.settings_total_stats_subtitle)
        ) {
            Text(
                text = formatCount(totalCount),
                style = MaterialTheme.typography.displaySmall,
                color = SebhaPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onRequestResetTotal)
                    .background(
                        color = androidx.compose.ui.graphics.Color(0xFFFDE8E8),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color(0xFFB3261E),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.settings_reset_total),
                        color = androidx.compose.ui.graphics.Color(0xFFB3261E),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSectionCard(
            icon = Icons.Outlined.Notifications,
            title = stringResource(R.string.settings_notifications_title),
            subtitle = stringResource(R.string.settings_notifications_subtitle)
        ) {
            SettingsSwitchRow(
                label = stringResource(R.string.vibration),
                checked = vibrationEnabled,
                onCheckedChange = onVibrationChange
            )
            SettingsSwitchRow(
                label = stringResource(R.string.sound),
                checked = soundEnabled,
                onCheckedChange = onSoundChange
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSectionCard(
            icon = Icons.Outlined.CalendarMonth,
            title = stringResource(R.string.settings_hijri_title),
            subtitle = stringResource(R.string.settings_hijri_subtitle)
        ) {
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
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SebhaPrimary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.settings_hijri_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = SebhaGold.copy(alpha = 0.12f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "☪",
                    fontSize = 20.sp,
                    color = SebhaGold
                )
                Text(
                    text = stringResource(R.string.settings_footer_dua),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SebhaPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = SebhaGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(SebhaPrimary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = SebhaPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = SebhaPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            content()
        }
    }
}

@Composable
private fun DailyProgressRing(
    progress: Float,
    percent: Int,
    modifier: Modifier = Modifier
) {
    val trackColor = SebhaProgressTrack
    val progressColor = SebhaPrimary

    Box(
        modifier = modifier.size(72.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = stroke
            )
        }
        Text(
            text = "$percent%",
            style = MaterialTheme.typography.labelLarge,
            color = SebhaPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LanguageChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium
            )
        },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = SebhaPrimary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp)
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
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = SebhaPrimary,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

private fun formatCount(value: Int): String =
    NumberFormat.getIntegerInstance(Locale.getDefault()).format(value)
