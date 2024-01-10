package com.umitytsr.peti.util

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

fun applyTheme(isDarkModeEnabled: Boolean) {
    val mode = if (isDarkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
    AppCompatDelegate.setDefaultNightMode(mode)
}

fun setAppLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val resources = context.resources
    val configuration = resources.configuration
    configuration.setLocale(locale)
    configuration.setLayoutDirection(locale)

    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        configuration.setLocale(locale)
    } else {
        configuration.locale = locale
    }

    @Suppress("DEPRECATION")
    resources.updateConfiguration(configuration, resources.displayMetrics)
}