package com.umitytsr.peti.view.home.add

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
class AddViewModel @Inject constructor(private val petiRepository: PetiRepository) : ViewModel() {

    private val _navigateResult = MutableStateFlow<Boolean>(false)
    val navigateResult : StateFlow<Boolean> = _navigateResult.asStateFlow()

    fun addPet(
        selectedPicture: Uri?, petDescription: String, petName: String, petType: String,
        petSex: String, petGoal: String, petAge: String, petVaccination: String, petBreed: String
    ) {
        viewModelScope.launch {
            petiRepository.addPet(selectedPicture, petDescription, petName, petType, petSex, petGoal,
                petAge, petVaccination, petBreed).collect{
                    _navigateResult.emit(it)
            }
        }
    }

    fun onNavigateDone(){
        viewModelScope.launch {
            _navigateResult.emit(false)
        }
    }
}