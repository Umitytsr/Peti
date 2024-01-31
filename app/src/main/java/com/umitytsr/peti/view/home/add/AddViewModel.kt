package com.umitytsr.peti.view.home.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umitytsr.peti.data.model.CityModel
import com.umitytsr.peti.data.repository.PetiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val petiRepository: PetiRepository
) : ViewModel() {

    private val _cityResult = MutableStateFlow<List<CityModel>>(emptyList())
    val cityResult = _cityResult.asStateFlow()

    private val _navigateResult = MutableStateFlow<Boolean>(false)
    val navigateResult: StateFlow<Boolean> = _navigateResult.asStateFlow()

    init {
        getCityData()
    }
    private fun getCityData(){
        viewModelScope.launch {
            petiRepository.fetchAllCity().collectLatest {
                _cityResult.emit(it)
            }
        }
    }

    fun addPet(
        selectedPetImage: Uri?, petName: String, petType: Long, petSex: Long, petGoal: Long,
        petAge: String, petVaccination: Long, petLocation: String,petBreed: String,
        petDescription: String
    ) {
        viewModelScope.launch {
            petiRepository.addPet(
                selectedPetImage, petName, petType, petSex, petGoal,
                petAge, petVaccination, petLocation ,petBreed, petDescription
            ).collect {
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