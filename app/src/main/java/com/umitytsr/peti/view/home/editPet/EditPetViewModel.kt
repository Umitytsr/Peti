package com.umitytsr.peti.view.home.editPet

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.umitytsr.peti.data.repository.PetiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPetViewModel @Inject constructor(
    private val petiRepository: PetiRepository
) : ViewModel() {

    private val _navigateResult = MutableStateFlow<Boolean>(false)
    val navigateResult: StateFlow<Boolean> = _navigateResult.asStateFlow()

    fun updatePet(
        oldPetPicture: String, newPetPicture: Uri?, petName: String, petType: Long, petSex: Long,
        petGoal: Long, petAge: String, petVaccination: Long, petBreed: String,
        petDescription: String, date: Timestamp?
    ) {
        viewModelScope.launch {
            petiRepository.updatePet(
                oldPetPicture, newPetPicture, petName, petType, petSex, petGoal, petAge,
                petVaccination, petBreed, petDescription, date
            )
                .collectLatest {
                    _navigateResult.emit(true)
                }
        }
    }

    fun onNavigateDone() {
        viewModelScope.launch {
            _navigateResult.emit(false)
        }
    }
}