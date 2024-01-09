package com.umitytsr.peti.view.home.add

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umitytsr.peti.data.repository.PetiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val petiRepository: PetiRepository,
    @ApplicationContext private val context: Context
    ) : ViewModel() {

    private val _navigateResult = MutableStateFlow<Boolean>(false)
    val navigateResult : StateFlow<Boolean> = _navigateResult.asStateFlow()

    fun addPet(
        selectedPicture: Uri?, petDescription: String, petName: String, petType: String,
        petSex: String, petGoal: String, petAge: String, petVaccination: String, petBreed: String
    ) {
        if (selectedPicture != null && petDescription.isNotEmpty() && petName.isNotEmpty() &&
            petType.isNotEmpty() && petSex.isNotEmpty() && petGoal.isNotEmpty() &&
            petAge.isNotEmpty() && petVaccination.isNotEmpty() && petBreed.isNotEmpty()
        ){
            viewModelScope.launch {
                petiRepository.addPet(selectedPicture, petDescription, petName, petType, petSex, petGoal,
                    petAge, petVaccination, petBreed).collect{
                    _navigateResult.emit(it)
                }
            }
        }else{
            Toast.makeText(context, "Please Fill in All Fields", Toast.LENGTH_SHORT).show()
        }
    }

    fun onNavigateDone(){
        viewModelScope.launch {
            _navigateResult.emit(false)
        }
    }
}