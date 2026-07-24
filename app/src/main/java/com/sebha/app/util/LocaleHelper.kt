package com.sebha.app.util

import android.content.Context
import android.content.res.Configuration
import com.sebha.app.data.AppLanguage
import java.util.Locale

/**
 * Applies the in-app language by wrapping the base context of each component.
 *
 * A lightweight SharedPreferences mirror is used so the locale can be read
 * synchronously from `attachBaseContext` (DataStore is async). This works on
 * every API level and does not depend on AppCompat.
 */
object LocaleHelper {

    private const val PREFS = "sebha_locale"
    private const val KEY = "language_code"

    /** Stores the selected language for synchronous locale wrapping. */
    fun persist(context: Context, code: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, AppLanguage.normalize(code))
            .apply()
    }

    /** Returns the persisted language, defaulting to French. */
    fun persistedLanguage(context: Context): String {
        val stored = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, null)
        return AppLanguage.normalize(stored)
    }

    /** Wraps [base] with a configuration forced to the persisted locale. */
    fun wrap(base: Context): Context {
        val locale = Locale.forLanguageTag(persistedLanguage(base))
        Locale.setDefault(locale)

        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return base.createConfigurationContext(config)
    }
}
