package com.sebha.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sebha.app.R
import com.sebha.app.data.ReligiousEvents
import com.sebha.app.data.ResolvedReligiousEvent
import com.sebha.app.ui.theme.SebhaGold
import com.sebha.app.ui.theme.SebhaPrimary
import com.sebha.app.util.HijriDateHelper
import java.util.Locale

/**
 * Religious holidays page: Today, next event, then a rolling 12-month list.
 */
@Composable
fun HolidaysPage(hijriCorrectionDays: Int) {
    val context = LocalContext.current
    val locale = context.resources.configuration.locales[0]
    val today = remember(hijriCorrectionDays) {
        HijriDateHelper.today(hijriCorrectionDays)
    }
    val todayEvents = remember(today) { ReligiousEvents.eventsOn(today) }
    val upcomingEvents = remember(today) {
        ReligiousEvents.upcomingWithinMonths(today, months = 12)
    }
    val nextEvent = upcomingEvents.firstOrNull()
    val laterEvents = upcomingEvents.drop(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 24.dp,
                top = 24.dp,
                end = 24.dp,
                bottom = 32.dp
            )
        ) {
            item {
                Text(
                    text = HijriDateHelper.formatGregorian(locale),
                    style = MaterialTheme.typography.titleMedium,
                    color = SebhaPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = HijriDateHelper.formatLong(today, locale.language, hijriCorrectionDays),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.holidays_today_section),
                    style = MaterialTheme.typography.titleLarge,
                    color = SebhaPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (todayEvents.isEmpty()) {
                    Text(
                        text = stringResource(R.string.holidays_no_event_today),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    todayEvents.forEach { event ->
                        HolidayCard(
                            event = event,
                            locale = locale,
                            hijriCorrectionDays = hijriCorrectionDays,
                            highlight = true,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.holidays_next_section),
                    style = MaterialTheme.typography.titleLarge,
                    color = SebhaPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (nextEvent != null) {
                    val days = ReligiousEvents.daysUntil(today, nextEvent.date)
                    HolidayCard(
                        event = nextEvent,
                        locale = locale,
                        hijriCorrectionDays = hijriCorrectionDays,
                        daysUntil = days,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (laterEvents.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(R.string.upcoming_religious_holidays),
                        style = MaterialTheme.typography.titleMedium,
                        color = SebhaPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            items(laterEvents, key = { "${it.year}-${it.definition.month}-${it.definition.day}-${it.definition.nameRes}" }) { event ->
                HolidaySimpleRow(
                    event = event,
                    locale = locale,
                    hijriCorrectionDays = hijriCorrectionDays,
                )
            }
        }
    }
}

@Composable
private fun HolidaySimpleRow(
    event: ResolvedReligiousEvent,
    locale: Locale,
    hijriCorrectionDays: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(76.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (event.definition.endDay != null) {
                    "${HijriDateHelper.dayOf(event.date)}–${HijriDateHelper.dayOf(event.endDate)}"
                } else {
                    HijriDateHelper.dayOf(event.date).toString()
                },
                style = MaterialTheme.typography.titleLarge,
                color = SebhaPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = HijriDateHelper.monthName(event.definition.month, locale.language),
                style = MaterialTheme.typography.labelMedium,
                color = SebhaGold,
                maxLines = 1
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(event.definition.nameRes),
                style = MaterialTheme.typography.titleMedium,
                color = SebhaPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = hijriDateLabel(event, locale.language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = HijriDateHelper.toGregorianLabel(
                    date = event.date,
                    locale = locale,
                    correctionDays = hijriCorrectionDays
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
    )
}

@Composable
private fun HolidayCard(
    event: ResolvedReligiousEvent,
    locale: Locale,
    hijriCorrectionDays: Int,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
    daysUntil: Long? = null
) {
    val definition = event.definition
    val dayLabel = if (definition.endDay != null) {
        "${HijriDateHelper.dayOf(event.date)}–${HijriDateHelper.dayOf(event.endDate)}"
    } else {
        HijriDateHelper.dayOf(event.date).toString()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) {
                SebhaPrimary.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(SebhaPrimary, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = dayLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = HijriDateHelper.monthName(definition.month, locale.language),
                        style = MaterialTheme.typography.labelSmall,
                        color = SebhaGold,
                        maxLines = 1
                    )
                }
            }

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = stringResource(definition.nameRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = SebhaPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = hijriDateLabel(event, locale.language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = HijriDateHelper.toGregorianLabel(
                        date = event.date,
                        locale = locale,
                        correctionDays = hijriCorrectionDays
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
                if (daysUntil != null && daysUntil > 0) {
                    Text(
                        text = stringResource(R.string.holidays_in_days, daysUntil),
                        style = MaterialTheme.typography.labelLarge,
                        color = SebhaGold,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun hijriDateLabel(event: ResolvedReligiousEvent, language: String): String {
    val month = HijriDateHelper.monthName(event.definition.month, language)
    val year = event.year
    return if (event.definition.endDay != null) {
        stringResource(
            R.string.hijri_holiday_date_range,
            HijriDateHelper.dayOf(event.date),
            HijriDateHelper.dayOf(event.endDate),
            month,
            year
        )
    } else {
        stringResource(
            R.string.hijri_holiday_date,
            HijriDateHelper.dayOf(event.date),
            month,
            year
        )
    }
}
