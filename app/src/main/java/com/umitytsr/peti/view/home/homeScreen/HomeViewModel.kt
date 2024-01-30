package com.umitytsr.peti.view.home.homeScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umitytsr.peti.data.model.FilteredPetModel
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.data.repository.PetiRepository
import com.umitytsr.peti.util.Enums
import com.umitytsr.peti.util.getIdForEnumString
import com.umitytsr.peti.util.getStringForEnumById
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
                    val petTypeString = getStringForEnumById<Enums.PetType>(it.petType!!,context)
                    petTypeString == selectedPetTypeText
                }
            }

            if (selectedPetSexText.isNotEmpty()) {
                filteredPet = filteredPet.filter {
                    val petSexString = getStringForEnumById<Enums.PetSex>(it.petSex!!,context)
                    petSexString == selectedPetSexText
                }
            }

            if (selectedPetGoalText.isNotEmpty()) {
                filteredPet = filteredPet.filter {
                    val petGoalString = getStringForEnumById<Enums.PetGoal>(it.petGoal!!,context)
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
                        getStringForEnumById<Enums.PetVaccination>(it.petVaccination!!,context)
                    petVacString == selectedPetVaccinationText
                }
            }

            _petListResult.emit(filteredPet)
        }

        viewModelScope.launch {
            val petTypeId = getIdForEnumString<Enums.PetType>(selectedPetTypeText,context)
            val petSexId = getIdForEnumString<Enums.PetSex>(selectedPetSexText,context)
            val petGoalId = getIdForEnumString<Enums.PetGoal>(selectedPetGoalText,context)
            val petVacId = getIdForEnumString<Enums.PetVaccination>(selectedPetVaccinationText,context)
            _filteredPet.emit(FilteredPetModel(petTypeId,petSexId,petGoalId,selectedPetAgeText,petVacId))
        }
    }

    fun clearFilters() {
        viewModelScope.launch {
            _filteredPet.emit(FilteredPetModel(null,null,null,null,null))
        }
    }
}