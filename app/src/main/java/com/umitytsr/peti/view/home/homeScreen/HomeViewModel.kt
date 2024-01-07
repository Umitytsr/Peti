package com.umitytsr.peti.view.home.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.data.repository.PetiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val petiRepository: PetiRepository) : ViewModel() {

    private val _petListResult = MutableStateFlow<List<PetModel>>(emptyList())
    val petListResult: StateFlow<List<PetModel>> = _petListResult.asStateFlow()

    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            petiRepository.fetchAllPets().collect{
                _petListResult.emit(it)
            }
        }
    }
}