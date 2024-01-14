package com.umitytsr.peti.view.home.petInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.umitytsr.peti.data.model.UserModel
import com.umitytsr.peti.data.repository.PetiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val petiRepository: PetiRepository,
    private val auth: FirebaseAuth
    ): ViewModel() {

    private val _userResult = MutableStateFlow<UserModel>(UserModel("","","",""))
    val userResult : StateFlow<UserModel> = _userResult.asStateFlow()

    private val _petOwnerEqualsResult = MutableStateFlow<Boolean>(false)
    val petOwnerEqualsResult : StateFlow<Boolean> = _petOwnerEqualsResult.asStateFlow()


    suspend fun getUserData(petOwnerEmail : String){
        viewModelScope.launch {
            petiRepository.fetchUser(petOwnerEmail).collect{user ->
                _userResult.emit(user)
            }
        }
    }

    suspend fun petOwnerEquals(petOwner : String){
        viewModelScope.launch {
            if (petOwner == auth.currentUser?.email){
                _petOwnerEqualsResult.emit(true)
            }
        }
    }
}