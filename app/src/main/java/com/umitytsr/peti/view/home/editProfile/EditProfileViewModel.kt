package com.umitytsr.peti.view.home.editProfile

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umitytsr.peti.R
import com.umitytsr.peti.data.repository.PetiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(private val petiRepository: PetiRepository,@ApplicationContext private val context: Context): ViewModel() {
    private val _navigateResult = MutableStateFlow<Boolean>(false)
    val navigateResult: StateFlow<Boolean> = _navigateResult.asStateFlow()

    fun updateUserInfo(userFirstName: String, userPhoneNumber: String, selectedPicture: Uri?) {
        if (userFirstName.isNotEmpty()){
            viewModelScope.launch {
                petiRepository.updateUser(userFirstName, userPhoneNumber, selectedPicture).collect {
                    _navigateResult.emit(it)
                }
            }
        }else{
            Toast.makeText(context, context.getString(R.string.please_enter_your_name), Toast.LENGTH_SHORT).show()
        }

    }

    fun onNavigateDone() {
        viewModelScope.launch {
            _navigateResult.emit(false)
        }
    }
}