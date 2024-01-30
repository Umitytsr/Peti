package com.umitytsr.peti.view.home.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umitytsr.peti.data.model.ChatCardModel
import com.umitytsr.peti.data.repository.PetiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val petiRepository: PetiRepository): ViewModel() {

    private val _chatListResult = MutableStateFlow<List<ChatCardModel>>(emptyList())
    val chatListResult : StateFlow<List<ChatCardModel>> = _chatListResult.asStateFlow()

    init {
        getChatListData()
    }

    private fun getChatListData(){
        viewModelScope.launch {
            petiRepository.fetchMyAllChat().collectLatest {
                _chatListResult.emit(it)
            }
        }
    }
}