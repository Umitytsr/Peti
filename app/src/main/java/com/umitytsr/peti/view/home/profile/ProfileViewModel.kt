package com.umitytsr.peti.view.home.profile

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umitytsr.peti.data.model.UserModel
import com.umitytsr.peti.data.repository.PetiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val petiRepository: PetiRepository) : ViewModel() {

    private val _userResult = MutableStateFlow<UserModel>(UserModel("","","",""))
    val userResult : StateFlow<UserModel> = _userResult.asStateFlow()

    init {
        getUserData()
    }

    private fun getUserData(){
        viewModelScope.launch {
            petiRepository.fetchUser(null).collect{user ->
                _userResult.emit(user)
            }
        }
    }

    fun signOut(requireActivity: Activity){
        viewModelScope.launch {
            petiRepository.signOut(requireActivity)
        }
    }

    fun deleteAccount(requireActivity: Activity){
        viewModelScope.launch {
            petiRepository.deleteAccount(requireActivity)
        }
    }
}