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

    init {
        loadDarkModePreference()
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
}