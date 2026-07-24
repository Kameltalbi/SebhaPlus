package com.sebha.app

import android.app.Application
import android.content.Context
import com.sebha.app.util.LocaleHelper

/**
 * Wraps the base context so app-level resources use the selected language.
 */
class SebhaApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.wrap(base))
    }
}
