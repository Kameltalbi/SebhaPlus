package com.sebha.app.data

import androidx.annotation.StringRes
import com.sebha.app.R
import com.sebha.app.util.HijriDateHelper
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoUnit

/**
 * Fixed Islamic observances defined by Hijri month/day (not Gregorian).
 */
data class ReligiousEventDefinition(
    @StringRes val nameRes: Int,
    val month: Int,
    val day: Int,
    /** Inclusive end day for multi-day events (null = single day). */
    val endDay: Int? = null
)

data class ResolvedReligiousEvent(
    val definition: ReligiousEventDefinition,
    val year: Int,
    val date: HijrahDate,
    val endDate: HijrahDate
)

object ReligiousEvents {

    /** Principal religious dates stored by Hijri calendar. */
    val definitions: List<ReligiousEventDefinition> = listOf(
        ReligiousEventDefinition(R.string.holiday_islamic_new_year, month = 1, day = 1),
        ReligiousEventDefinition(R.string.holiday_ashura, month = 1, day = 10),
        ReligiousEventDefinition(R.string.holiday_mawlid, month = 3, day = 12),
        ReligiousEventDefinition(R.string.holiday_rajab_start, month = 7, day = 1),
        ReligiousEventDefinition(R.string.holiday_isra_miraj, month = 7, day = 27),
        ReligiousEventDefinition(R.string.holiday_mid_shaban, month = 8, day = 15),
        ReligiousEventDefinition(R.string.holiday_ramadan_start, month = 9, day = 1),
        ReligiousEventDefinition(R.string.holiday_battle_of_badr, month = 9, day = 17),
        ReligiousEventDefinition(R.string.holiday_night_21, month = 9, day = 21),
        ReligiousEventDefinition(R.string.holiday_night_23, month = 9, day = 23),
        ReligiousEventDefinition(R.string.holiday_night_25, month = 9, day = 25),
        ReligiousEventDefinition(R.string.holiday_laylat_al_qadr, month = 9, day = 27),
        // End of Ramadan uses the last day of the month (29 or 30).
        ReligiousEventDefinition(R.string.holiday_ramadan_end, month = 9, day = 29),
        ReligiousEventDefinition(R.string.holiday_eid_al_fitr, month = 10, day = 1),
        ReligiousEventDefinition(R.string.holiday_hajj_start, month = 12, day = 8),
        ReligiousEventDefinition(R.string.holiday_arafah, month = 12, day = 9),
        ReligiousEventDefinition(R.string.holiday_eid_al_adha, month = 12, day = 10),
        ReligiousEventDefinition(R.string.holiday_tashreeq, month = 12, day = 11, endDay = 13)
    )

    fun resolveForYear(year: Int): List<ResolvedReligiousEvent> {
        return definitions.mapNotNull { definition ->
            resolve(definition, year)
        }.sortedBy { it.date }
    }

    fun eventsOn(date: HijrahDate): List<ResolvedReligiousEvent> {
        val year = HijriDateHelper.yearOf(date)
        return resolveForYear(year).filter { event ->
            !date.isBefore(event.date) && !date.isAfter(event.endDate)
        }
    }

    fun nextAfter(date: HijrahDate): ResolvedReligiousEvent? {
        val year = HijriDateHelper.yearOf(date)
        val thisYear = resolveForYear(year)
        thisYear.firstOrNull { it.date.isAfter(date) }?.let { return it }
        return resolveForYear(year + 1).firstOrNull()
    }

    /** All events strictly after [date], over the next rolling Hijri months. */
    fun upcomingWithinMonths(
        date: HijrahDate,
        months: Long
    ): List<ResolvedReligiousEvent> {
        val startYear = HijriDateHelper.yearOf(date)
        val endExclusive = date.plus(months, ChronoUnit.MONTHS)
        return (startYear..HijriDateHelper.yearOf(endExclusive))
            .flatMap(::resolveForYear)
            .filter { event ->
                event.date.isAfter(date) && event.date.isBefore(endExclusive)
            }
            .sortedBy { it.date }
    }

    fun daysUntil(from: HijrahDate, to: HijrahDate): Long =
        ChronoUnit.DAYS.between(from, to)

    private fun resolve(
        definition: ReligiousEventDefinition,
        year: Int
    ): ResolvedReligiousEvent? {
        val day = when {
            definition.month == 9 && definition.nameRes == R.string.holiday_ramadan_end ->
                HijriDateHelper.lengthOfMonth(year, 9)
            else -> definition.day
        }
        val length = HijriDateHelper.lengthOfMonth(year, definition.month)
        if (day !in 1..length) return null

        val start = HijriDateHelper.of(year, definition.month, day)
        val endDay = (definition.endDay ?: day).coerceAtMost(length)
        val end = HijriDateHelper.of(year, definition.month, endDay)
        return ResolvedReligiousEvent(
            definition = definition,
            year = year,
            date = start,
            endDate = end
        )
    }
}
