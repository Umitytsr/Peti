package com.umitytsr.peti.view.home.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.data.model.UserModel
import com.umitytsr.peti.data.repository.PetiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val petiRepository: PetiRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _petListResult = MutableStateFlow<List<PetModel>>(emptyList())
    val petListResult : StateFlow<List<PetModel>> = _petListResult.asStateFlow()

    private val _userResult = MutableStateFlow<UserModel>(UserModel("","","",""))
    val userResult : StateFlow<UserModel> = _userResult.asStateFlow()

    init {
        getPetListData()
        getUserData()
    }

    private fun getPetListData(){
        viewModelScope.launch {
            petiRepository.fetchAllPets().collect { petList ->
                val currentUser = auth.currentUser!!.email
                val filteredPet = petList.filter { petModel ->
                    petModel.petOwner == currentUser
                }
                _petListResult.emit(filteredPet)
            }
        }
    }

    private fun getUserData(){
        viewModelScope.launch {
            petiRepository.fetchUser().collect{user ->
                _userResult.emit(user)
            }
        }
    }
}