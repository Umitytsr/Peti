package com.umitytsr.peti.view.home.myPets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.data.repository.PetiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPetsViewModel @Inject constructor(private val petiRepository: PetiRepository,private val auth: FirebaseAuth):ViewModel() {

    private val _petListResult = MutableStateFlow<List<PetModel>>(emptyList())
    val petListResult : StateFlow<List<PetModel>> = _petListResult.asStateFlow()

    init {
        getPetListData()
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

    fun deletePet(petImage : String){
        viewModelScope.launch {
            petiRepository.deletePet(petImage)
        }
    }
}