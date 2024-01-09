package com.umitytsr.peti.view.home.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umitytsr.peti.data.repository.PetiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val petiRepository: PetiRepository) : ViewModel() {

    private val _navigateResult = MutableStateFlow<Boolean>(false)
    val navigateResult: StateFlow<Boolean> = _navigateResult.asStateFlow()

    fun updateUserInfo(userFirstName: String, userPhoneNumber: String, selectedPicture: Uri?) {
        viewModelScope.launch {
            petiRepository.updateUser(userFirstName, userPhoneNumber, selectedPicture).collect {
                _navigateResult.emit(it)
            }
        }
    }

    fun onNavigateDone() {
        viewModelScope.launch {
            _navigateResult.emit(false)
        }
    }
}