package ru.behetem.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.behetem.models.ChatRoomModel

class ChatViewModel: BaseViewModel() {

    private val chatRoomLiveData: MutableLiveData<ChatRoomModel> = MutableLiveData()

    fun onMessageTextChanged(charSequence: CharSequence) {

    }

    fun setChatRoomLiveData(chatRoom: ChatRoomModel) {
        chatRoomLiveData.value = chatRoom
    }

    fun getChatRoomLiveData(): LiveData<ChatRoomModel> {
        return chatRoomLiveData
    }
}