package com.sebha.app.data

/**
 * App language codes persisted in DataStore and applied via AppCompat locales.
 */
object AppLanguage {
    const val FRENCH = "fr"
    const val ENGLISH = "en"
    const val ARABIC = "ar"

    val all = listOf(FRENCH, ENGLISH, ARABIC)

    fun normalize(code: String?): String = when (code) {
        ENGLISH, ARABIC, FRENCH -> code
        else -> FRENCH
    }
}
