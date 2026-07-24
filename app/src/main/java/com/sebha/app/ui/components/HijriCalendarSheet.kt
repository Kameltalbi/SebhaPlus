package com.sebha.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebha.app.R
import com.sebha.app.data.DailyDua
import com.sebha.app.data.DailyDuas
import com.sebha.app.data.SourceType
import com.sebha.app.ui.theme.SebhaGold
import com.sebha.app.ui.theme.SebhaPrimary
import com.sebha.app.util.HijriDateHelper

/**
 * Hijri month calendar.
 * Always derived from [hijriCorrectionDays] so it stays synced with Settings.
 */
@Composable
fun HijriCalendarPage(hijriCorrectionDays: Int) {
    val language = LocalContext.current.resources.configuration.locales[0].language
    val today = remember(hijriCorrectionDays) { HijriDateHelper.today(hijriCorrectionDays) }
    val todayYear = HijriDateHelper.yearOf(today)
    val todayMonth = HijriDateHelper.monthOf(today)
    val todayDay = HijriDateHelper.dayOf(today)

    var visibleYear by remember { mutableIntStateOf(todayYear) }
    var visibleMonth by remember { mutableIntStateOf(todayMonth) }

    // When the user changes the Hijri correction, jump back to the new "today" month.
    LaunchedEffect(hijriCorrectionDays) {
        visibleYear = todayYear
        visibleMonth = todayMonth
    }

    val daysInMonth = HijriDateHelper.lengthOfMonth(visibleYear, visibleMonth)
    val startOffset = HijriDateHelper.firstWeekdayOffset(visibleYear, visibleMonth, hijriCorrectionDays)
    val cells = startOffset + daysInMonth
    val rowCount = (cells + 6) / 7

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
                text = stringResource(R.string.hijri_calendar),
                style = MaterialTheme.typography.titleLarge,
                color = SebhaPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(
                    R.string.hijri_calendar_today,
                    HijriDateHelper.formatLong(today, language, hijriCorrectionDays)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (hijriCorrectionDays != 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.hijri_calendar_correction_label,
                        if (hijriCorrectionDays > 0) "+$hijriCorrectionDays" else "$hijriCorrectionDays"
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = SebhaGold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Month navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    if (visibleMonth == 1) {
                        visibleMonth = 12
                        visibleYear -= 1
                    } else {
                        visibleMonth -= 1
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.previous_month)
                    )
                }

                Text(
                    text = HijriDateHelper.title(visibleYear, visibleMonth, language),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = {
                    if (visibleMonth == 12) {
                        visibleMonth = 1
                        visibleYear += 1
                    } else {
                        visibleMonth += 1
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.next_month)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Weekday headers
            Row(modifier = Modifier.fillMaxWidth()) {
                HijriDateHelper.weekdayShort(language).forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Day grid
            repeat(rowCount) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(7) { column ->
                        val cellIndex = row * 7 + column
                        val dayNumber = cellIndex - startOffset + 1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dayNumber in 1..daysInMonth) {
                                val isToday = visibleYear == todayYear &&
                                    visibleMonth == todayMonth &&
                                    dayNumber == todayDay

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .then(
                                            if (isToday) {
                                                Modifier
                                                    .background(SebhaPrimary)
                                                    .border(1.5.dp, SebhaGold, CircleShape)
                                            } else {
                                                Modifier
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        color = if (isToday) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            val todayDua = remember(todayMonth, todayDay) {
                DailyDuas.forDate(todayMonth, todayDay)
            }
            if (todayDua != null) {
                DailyDuaCard(dua = todayDua)
            }
    }
}

@Composable
private fun DailyDuaCard(dua: DailyDua) {
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
                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                        contentDescription = null,
                        tint = SebhaPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = stringResource(R.string.daily_dua_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = SebhaPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = dua.theme,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (dua.occasion.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = dua.occasion,
                    style = MaterialTheme.typography.labelLarge,
                    color = SebhaGold,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = dua.duaAr,
                style = MaterialTheme.typography.titleLarge.copy(
                    textDirection = TextDirection.Rtl
                ),
                color = SebhaPrimary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                lineHeight = 34.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = dua.transliteration,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = dua.translationFr,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(
                    R.string.daily_dua_source,
                    dua.source,
                    when (dua.sourceType) {
                        SourceType.QURAN -> stringResource(R.string.source_type_quran)
                        SourceType.HADITH -> stringResource(R.string.source_type_hadith)
                    }
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
