package com.umitytsr.peti.view.authentication.mainActivity

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umitytsr.peti.util.getDefaultLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val sharedPreferences: SharedPreferences): ViewModel(){
    private val _isCheckedResult = MutableStateFlow(false)
    val isCheckedResult = _isCheckedResult.asStateFlow()

    private val _isLanguageString = MutableStateFlow(getDefaultLanguage())
    val isLanguageString = _isLanguageString.asStateFlow()

    private val _isLanguageId = MutableStateFlow(1)
    val isLanguageId = _isLanguageId.asStateFlow()

    init {
        loadDarkModePreference()
        loadLanguagePreference()
    }

    fun setDarkModeEnabled(isChecked: Boolean) {
        viewModelScope.launch {
            _isCheckedResult.emit(isChecked)
            sharedPreferences.edit().putBoolean("darkModeEnabled", isChecked).apply()
        }
    }

    private fun loadDarkModePreference() {
        val isDarkModeEnabled = sharedPreferences.getBoolean("darkModeEnabled", false)
        viewModelScope.launch {
            _isCheckedResult.emit(isDarkModeEnabled)
        }
    }

    private fun getLanguageId(language: String): Int {
        return when (language) {
            "en" -> 1
            "tr" -> 2
            "fr" -> 3
            "es" -> 4
            else -> 2
        }
    }

    fun setLanguageString(language: String) {
        viewModelScope.launch {
            _isLanguageString.emit(language)
            sharedPreferences.edit().putString("languageString", language).apply()
            _isLanguageId.emit(getLanguageId(language))
        }
    }

    private fun loadLanguagePreference() {
        val language = sharedPreferences.getString("languageString", getDefaultLanguage()) ?: getDefaultLanguage()
        val languageId = sharedPreferences.getInt("languageId", getLanguageId(language))
        viewModelScope.launch {
            _isLanguageString.emit(language)
            _isLanguageId.emit(languageId)
        }
    }
}