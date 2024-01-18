package com.umitytsr.peti.view.home.homeScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umitytsr.peti.data.model.FilteredPetModel
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.data.repository.PetiRepository
import com.umitytsr.peti.util.Enums
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val petiRepository: PetiRepository) : ViewModel() {

    private val _petListResult = MutableStateFlow<List<PetModel>>(emptyList())
    val petListResult: StateFlow<List<PetModel>> = _petListResult.asStateFlow()

    private val _filteredPet =
        MutableStateFlow<FilteredPetModel>(FilteredPetModel(null, null, null, null, null))
    val filteredPet = _filteredPet.asStateFlow()


    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            petiRepository.fetchAllPets().collect {
                _petListResult.emit(it)
            }
        }
    }

    fun filterPets(
        selectedPetTypeText: String,
        selectedPetSexText: String,
        selectedPetGoalText: String,
        selectedPetAgeText: String,
        selectedPetVaccinationText: String,
        context: Context
    ) {
        viewModelScope.launch {
            val allPets = petiRepository.fetchAllPets().first()
            var filteredPet = allPets

            if (selectedPetTypeText.isNotEmpty()) {
                filteredPet = filteredPet.filter {
                    val petTypeString = Enums.getPetTypeId(it.petType).getString(context)
                    petTypeString == selectedPetTypeText
                }
            }

            if (selectedPetSexText.isNotEmpty()) {
                filteredPet = filteredPet.filter {
                    val petSexString = Enums.getPetSexById(it.petSex).getString(context)
                    petSexString == selectedPetSexText
                }
            }

            if (selectedPetGoalText.isNotEmpty()) {
                filteredPet = filteredPet.filter {
                    val petGoalString = Enums.getPetGoalById(it.petGoal).getString(context)
                    petGoalString == selectedPetGoalText
                }
            }

            if (selectedPetAgeText.isNotEmpty()) {
                filteredPet = filteredPet.filter {
                    val petAgeString = it.petAge
                    petAgeString == selectedPetAgeText
                }
            }

            if (selectedPetVaccinationText.isNotEmpty()) {
                filteredPet = filteredPet.filter {
                    val petVacString =
                        Enums.getPetVaccinationById(it.petVaccination).getString(context)
                    petVacString == selectedPetVaccinationText
                }
            }

            _petListResult.emit(filteredPet)
        }

        viewModelScope.launch {
            val petTypeId = Enums.getPetTypeIdByString(selectedPetTypeText,context)
            val petSexId = Enums.getPetSexIdByString(selectedPetSexText,context)
            val petGoalId = Enums.getPetGoalIdByString(selectedPetGoalText,context)
            val petVacId = Enums.getPetVaccinationIdByString(selectedPetVaccinationText,context)
            _filteredPet.emit(FilteredPetModel(petTypeId,petSexId,petGoalId,selectedPetAgeText,petVacId))
        }
    }

    fun clearFilters() {
        viewModelScope.launch {
            _filteredPet.emit(FilteredPetModel(null,null,null,null,null))
        }
    }
}