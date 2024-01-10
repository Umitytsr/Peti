package com.umitytsr.peti.view.authentication.mainActivity

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val sharedPreferences: SharedPreferences): ViewModel(){
    private val _isCheckedResult = MutableStateFlow(false)
    val isCheckedResult = _isCheckedResult.asStateFlow()

    private val _isLanguageString = MutableStateFlow("en")
    val isLanguageString = _isLanguageString.asStateFlow()

    private val _isLanguageId = MutableStateFlow(1)
    val isLanguageId = _isLanguageId.asStateFlow()

    init {
        loadDarkModePreference()
        setLanguagePreference()
        setLanguagePreferenceId()
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

    fun setLanguageString(language : String){
        viewModelScope.launch {
            _isLanguageString.emit(language)
            sharedPreferences.edit().putString("languageString",language).apply()
        }
    }

    private fun setLanguagePreference(){
        val isLanguageDefault = sharedPreferences.getString("languageString","en")
        viewModelScope.launch {
            if (isLanguageDefault != null) {
                _isLanguageString.emit(isLanguageDefault)
            }
        }
    }

    fun setLanguageId(languageId : Int){
        viewModelScope.launch {
            _isLanguageId.emit(languageId)
            sharedPreferences.edit().putInt("languageId",languageId).apply()
        }
    }

    private fun setLanguagePreferenceId(){
        val isLanguageDefault = sharedPreferences.getInt("languageId",1)
        viewModelScope.launch {
            if (isLanguageDefault != null) {
                _isLanguageId.emit(isLanguageDefault)
            }
        }
    }
}