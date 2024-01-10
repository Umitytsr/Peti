package com.umitytsr.peti.util

import android.content.Context
import android.preference.PreferenceManager

object LanguageManager {
    private const val SELECTED_LANGUAGE_KEY = "selected_language"
    private const val DEFAULT_LANGUAGE = "en" // Varsayılan dil (örneğin, İngilizce)

    fun getSelectedLanguage(context: Context): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(SELECTED_LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    fun setSelectedLanguage(context: Context, languageCode: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putString(SELECTED_LANGUAGE_KEY, languageCode).apply()
    }
}