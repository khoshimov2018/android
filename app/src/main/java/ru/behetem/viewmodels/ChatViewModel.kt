package ru.behetem.viewmodels

import android.app.Application
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.behetem.models.ChatMessageModel
import ru.behetem.models.ChatRoomModel
import ru.behetem.models.ReactionModel
import ru.behetem.repositories.ChatsRepository
import ru.behetem.repositories.ReactionsRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.*

class ChatViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val chatRoomLiveData: MutableLiveData<ChatRoomModel> = MutableLiveData()
    private val chatsListingLiveData: MutableLiveData<MutableList<ChatMessageModel>> =
        MutableLiveData()

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>

    private lateinit var sendMessageApiResponse: LiveData<BaseResponse>
    private lateinit var sendMessageObserveResponse: Observer<BaseResponse>

    private val chatMessageModel = ChatMessageModel()

    fun getLatestMessages() {
        if (chatRoomLiveData.value?.page == null) {
            chatRoomLiveData.value?.page = 0
        }

        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            observeResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
                    if (it.data is MutableList<*>) {
                        val gson = Gson()
                        val strResponse = gson.toJson(it.data)
                        val myType = object : TypeToken<MutableList<ChatMessageModel>>() {}.type
                        val chatsList: MutableList<ChatMessageModel> =
                            gson.fromJson<MutableList<ChatMessageModel>>(strResponse, myType)

                        /*for (reaction in reactionsList) {
                            reaction.image = "${ApiConstants.BASE_URL}${reaction.image}"
                        }*/

                        chatsListingLiveData.value = chatsList
                    }
                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            chatRoomLiveData.value?.let {
                apiResponse =
                    ChatsRepository.getMessages(strToken, it.recipientId!!, 0, it.pageSize)
            }
            apiResponse.observeForever(observeResponse)
        } else {
            showNoInternet.value = true
        }
    }

    fun onMessageTextChanged(charSequence: CharSequence) {
        chatMessageModel.content = charSequence.toString()
    }

    fun onSendMessage(view: View, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            chatMessageModel.recipientId = chatRoomLiveData.value?.recipientId
            if (!chatMessageModel.content.isNullOrEmpty()) {
                sendMessage()
            }
            return true
        }
        return false
    }

    private fun sendMessage() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            sendMessageObserveResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {

                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            sendMessageApiResponse = ChatsRepository.sendMessage(strToken, chatMessageModel)
            sendMessageApiResponse.observeForever(sendMessageObserveResponse)
        } else {
            showNoInternet.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (this::apiResponse.isInitialized) {
            apiResponse.removeObserver(observeResponse)
        }
        if (this::sendMessageApiResponse.isInitialized) {
            sendMessageApiResponse.removeObserver(sendMessageObserveResponse)
        }
    }

    fun isMessageSent(senderId: String): Boolean {
        return chatRoomLiveData.value?.senderId.equals(senderId)
    }

    fun getMaxWidth(): Int {
        val deviceMax = getDeviceMaxWidth(context)
        return (deviceMax * Constants.MAX_WIDTH_FOR_CHAT_PERCENTAGE).toInt()
    }

    fun setChatRoomLiveData(chatRoom: ChatRoomModel) {
        chatRoomLiveData.value = chatRoom
    }

    fun getChatRoomLiveData(): LiveData<ChatRoomModel> {
        return chatRoomLiveData
    }

    fun getChatsListingLiveData(): LiveData<MutableList<ChatMessageModel>> {
        return chatsListingLiveData
    }
}