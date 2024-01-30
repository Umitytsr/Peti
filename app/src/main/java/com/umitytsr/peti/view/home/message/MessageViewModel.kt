package com.umitytsr.peti.view.home.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.umitytsr.peti.data.model.Message
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.data.model.UserModel
import com.umitytsr.peti.data.repository.PetiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(private val petiRepository: PetiRepository) :
    ViewModel() {

    private val _messageResult = MutableStateFlow<List<Message>>(emptyList())
    val messageResult = _messageResult.asStateFlow()

    private val _userResult = MutableStateFlow<UserModel>(UserModel("","","",""))
    val userResult : StateFlow<UserModel> = _userResult.asStateFlow()

    private val _petResult = MutableStateFlow<PetModel>(PetModel("" ,"",
        "",0,0,0,"",0,
        "","", Timestamp.now()))
    val petResult : StateFlow<PetModel> = _petResult.asStateFlow()

    suspend fun getPetData(petOwnerEmail: String, petName: String) {
        viewModelScope.launch {
            petiRepository.fetchPet(petOwnerEmail,petName).collect{pet ->
                _petResult.emit(pet)
            }
        }
    }

    fun getReceiverInfo(receiverEmail: String){
        viewModelScope.launch {
            petiRepository.fetchUser(receiverEmail).collectLatest {
                _userResult.emit(it)
            }
        }
    }

    fun fetchMyAllMessageWithInfoFragment(petName: String, petOwnerEmail: String) {
        viewModelScope.launch {
            petiRepository.fetchMyAllMessageWithInfoFragment(petName, petOwnerEmail).collectLatest {
                _messageResult.emit(it)
            }
        }
    }

    fun fetchMyAllMessageWithChatFragment(receiverEmail: String, docPath: String) {
        viewModelScope.launch {
            petiRepository.fetchMyAllMessageWithChatFragment(receiverEmail, docPath).collectLatest {
                _messageResult.emit(it)
            }
        }
    }

    fun createChat(petName: String, petOwnerEmail: String) {
        viewModelScope.launch {
            petiRepository.createChat(petName, petOwnerEmail)
        }
    }

    fun sendMessageFromInfoFragment(petName: String, petOwnerEmail: String, message: String) {
        viewModelScope.launch {
            petiRepository.sendMessageFromInfoFragment(petName, petOwnerEmail, message)
        }
    }

    fun sendMessageFromChatFragment(receiverEmail: String, chatDocPath: String, message: String) {
        viewModelScope.launch {
            petiRepository.sendMessageFromChatFragment(receiverEmail, chatDocPath, message)
        }
    }
}