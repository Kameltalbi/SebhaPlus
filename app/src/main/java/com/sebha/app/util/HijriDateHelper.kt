package com.sebha.app.util

import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Helpers around [HijrahDate] (Umm Al-Qura) with optional day-level correction.
 * All calendar views must use [today] / [of] so they stay synced with settings.
 */
object HijriDateHelper {

    /** Arabic weekday + month names for authentic Hijri display. */
    private val arabicWeekdays = listOf(
        "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت", "الأحد"
    )

    /** Short Arabic weekday labels for the calendar header (Mon → Sun). */
    val arabicWeekdayShort = listOf("إث", "ثل", "أر", "خم", "جم", "سب", "أح")

    private val latinWeekdayShort = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")
    private val englishWeekdayShort = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    private val arabicWeekdaysFull = listOf(
        "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت", "الأحد"
    )
    private val frenchWeekdaysFull = listOf(
        "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"
    )
    private val englishWeekdaysFull = listOf(
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    )

    private val arabicMonths = listOf(
        "محرم", "صفر", "ربيع الأول", "ربيع الثاني",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
    )

    private val frenchMonths = listOf(
        "Mouharram", "Safar", "Rabî al-Awwal", "Rabî ath-Thânî",
        "Joumada al-Oûla", "Joumada ath-Thânia", "Rajab", "Chaabane",
        "Ramadan", "Chawwal", "Dhou al-Qi'da", "Dhou al-Hijja"
    )

    private val englishMonths = listOf(
        "Muharram", "Safar", "Rabi al-Awwal", "Rabi ath-Thani",
        "Jumada al-Ula", "Jumada ath-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    /** True for Arabic language tag. */
    private fun isArabic(language: String) = language.startsWith("ar")

    /** Localized month name for the given language ("fr"/"en"/"ar"). */
    fun monthName(month: Int, language: String): String = when {
        isArabic(language) -> arabicMonths[month - 1]
        language.startsWith("en") -> englishMonths[month - 1]
        else -> frenchMonths[month - 1]
    }

    private fun eraSuffix(language: String) = if (isArabic(language)) "هـ" else "AH"

    /** Localized "month year" title, e.g. "Safar 1448 AH" / "صفر 1448 هـ". */
    fun title(year: Int, month: Int, language: String): String =
        "${monthName(month, language)} $year ${eraSuffix(language)}"

    /** Short weekday labels (Mon → Sun) for the calendar header. */
    fun weekdayShort(language: String): List<String> = when {
        isArabic(language) -> arabicWeekdayShort
        language.startsWith("en") -> englishWeekdayShort
        else -> latinWeekdayShort
    }

    /**
     * Localized long date, e.g. "Dimanche 5 Safar 1448 AH".
     *
     * [correctionDays] only shifts the Hijri *label*; the weekday must reflect the
     * real current day, so it is shifted back by the same amount.
     */
    fun formatLong(date: HijrahDate, language: String, correctionDays: Int = 0): String {
        val rawIndex = date.get(ChronoField.DAY_OF_WEEK) - 1
        val weekdayIndex = ((rawIndex - correctionDays) % 7 + 7) % 7
        val weekday = when {
            isArabic(language) -> arabicWeekdaysFull[weekdayIndex]
            language.startsWith("en") -> englishWeekdaysFull[weekdayIndex]
            else -> frenchWeekdaysFull[weekdayIndex]
        }
        val day = dayOf(date)
        val month = monthName(monthOf(date), language)
        val year = yearOf(date)
        return "$weekday $day $month $year ${eraSuffix(language)}"
    }

    /** Today's Hijri date after applying [correctionDays] (−2…+2). */
    fun today(correctionDays: Int): HijrahDate {
        return HijrahChronology.INSTANCE.dateNow()
            .plus(correctionDays.toLong(), ChronoUnit.DAYS)
    }

    /** Builds a Hijri date for the given AH year / month / day. */
    fun of(year: Int, month: Int, day: Int): HijrahDate {
        return HijrahChronology.INSTANCE.date(year, month, day)
    }

    fun yearOf(date: HijrahDate): Int = date.get(ChronoField.YEAR)

    fun monthOf(date: HijrahDate): Int = date.get(ChronoField.MONTH_OF_YEAR)

    fun dayOf(date: HijrahDate): Int = date.get(ChronoField.DAY_OF_MONTH)

    fun lengthOfMonth(year: Int, month: Int): Int =
        of(year, month, 1).range(ChronoField.DAY_OF_MONTH).maximum.toInt()

    /**
     * Day-of-week index 0 = Monday … 6 = Sunday for the 1st of the month.
     * Used to pad empty cells in the calendar grid.
     *
     * [correctionDays] only relabels Hijri days, so the real weekday of a given
     * label is shifted back by the correction to keep weekdays fixed.
     */
    fun firstWeekdayOffset(year: Int, month: Int, correctionDays: Int = 0): Int {
        val rawIndex = of(year, month, 1).get(ChronoField.DAY_OF_WEEK) - 1
        return ((rawIndex - correctionDays) % 7 + 7) % 7
    }

    fun monthNameArabic(month: Int): String = arabicMonths[month - 1]

    fun titleArabic(year: Int, month: Int): String =
        "${monthNameArabic(month)} $year هـ"

    /** Stable key "AH-year-month" used to remember dialog answers. */
    fun monthKey(date: HijrahDate): String {
        val year = yearOf(date)
        val month = monthOf(date)
        return "$year-${month.toString().padStart(2, '0')}"
    }

    /**
     * True when the corrected Hijri day is 29 and we have not yet asked
     * about this particular month.
     */
    fun shouldPromptNewMonth(
        date: HijrahDate,
        dismissedKey: String
    ): Boolean = dayOf(date) == 29 && monthKey(date) != dismissedKey

    /**
     * Formats a Hijri date in Arabic script, e.g. "الأحد 4 صفر 1448 هـ".
     */
    fun formatArabic(date: HijrahDate): String {
        val weekday = arabicWeekdays[date.get(ChronoField.DAY_OF_WEEK) - 1]
        val day = dayOf(date)
        val month = monthNameArabic(monthOf(date))
        val year = yearOf(date)
        return "$weekday $day $month $year هـ"
    }

    /**
     * Gregorian long date localized for the given [locale].
     * Example (fr): "Dimanche 19 juillet 2026"
     */
    fun formatGregorian(locale: Locale = Locale.getDefault()): String {
        val formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", locale)
        return java.time.LocalDate.now().format(formatter).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        }
    }

    /**
     * Gregorian equivalent of a displayed Hijri day.
     * The correction is subtracted because it advances the displayed Hijri date.
     */
    fun toGregorianLabel(
        date: HijrahDate,
        locale: Locale,
        correctionDays: Int = 0
    ): String {
        val gregorian = java.time.LocalDate.from(date).minusDays(correctionDays.toLong())
        val formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", locale)
        return gregorian.format(formatter).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        }
    }
}
