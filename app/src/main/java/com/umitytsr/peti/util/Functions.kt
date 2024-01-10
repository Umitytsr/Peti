package com.umitytsr.peti.util

import androidx.appcompat.app.AppCompatDelegate

fun applyTheme(isDarkModeEnabled: Boolean) {
    val mode = if (isDarkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
    AppCompatDelegate.setDefaultNightMode(mode)
}