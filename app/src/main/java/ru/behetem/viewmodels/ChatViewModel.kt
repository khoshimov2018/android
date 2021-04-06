package ru.behetem.viewmodels

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.behetem.activities.ReceivedReactionDetailActivity
import ru.behetem.models.ChatMessageModel
import ru.behetem.models.ChatRoomModel
import ru.behetem.models.ReactionModel
import ru.behetem.repositories.ChatsRepository
import ru.behetem.repositories.ReactionsRepository
import ru.behetem.repositories.UserRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.*
import java.util.*

class ChatViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val chatRoomLiveData: MutableLiveData<ChatRoomModel> = MutableLiveData()
    private val chatsListingLiveData: MutableLiveData<MutableList<ChatMessageModel>> = MutableLiveData(ArrayList())

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>

    private lateinit var sendMessageApiResponse: LiveData<BaseResponse>
    private lateinit var sendMessageObserveResponse: Observer<BaseResponse>

    private lateinit var uploadImageApiResponse: LiveData<BaseResponse>
    private lateinit var uploadImageObserveResponse: Observer<BaseResponse>

    private val chatMessageModel = ChatMessageModel()

    private val isPullToRefreshLoading: MutableLiveData<Boolean> = MutableLiveData()
    private var shouldHitPagination = true
    val messageToBeSent: MutableLiveData<String> = MutableLiveData()
    private val shouldMoveToBottom: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var view: View
    private val openImagePicker: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var timer: Timer
    private var shouldHitTimer = true

    fun startTimer() {
        if (!this::timer.isInitialized) {
            timer = Timer()
        }
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                getLatestMessages()
            }
        }, 0L, Constants.MESSENGER_TIME_OUT)
    }

    fun getLatestMessages() {
        if (isInternetAvailable(context)) {
            showNoInternet.postValue(false)
//            loaderVisible.value = true // show loader

            observeResponse = Observer<BaseResponse> {
//                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
                    if (it.data is MutableList<*>) {
                        val gson = Gson()
                        val strResponse = gson.toJson(it.data)
                        val myType = object : TypeToken<MutableList<ChatMessageModel>>() {}.type
                        val chatsList: MutableList<ChatMessageModel> =
                            gson.fromJson<MutableList<ChatMessageModel>>(strResponse, myType)

                        chatsList.reverse()
                        val newMessagesToAdd = getNewMessagesToAdd(chatsList)
                        if(newMessagesToAdd.size > 0) {
                            chatsListingLiveData.value?.addAll(newMessagesToAdd)
                            chatsListingLiveData.postValue(chatsListingLiveData.value)
                            shouldMoveToBottom.postValue(true)
                        }

                        if (chatsList.size < Constants.PAGE_SIZE) {
                            shouldHitPagination = false
                        }

                        if (chatRoomLiveData.value?.page == null) {
                            chatRoomLiveData.value?.page = 1
                        }
                    }
                } else {
                    baseResponse.postValue(it)
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            chatRoomLiveData.value?.let {
                apiResponse =
                    ChatsRepository.getMessages(strToken, it.recipientId!!, 0, it.pageSize)
            }
            Handler(Looper.getMainLooper()).post {
                apiResponse.observeForever(observeResponse)
            }
        } else {
            showNoInternet.postValue(true)
        }
    }

    private fun getNewMessagesToAdd(receivedMessages: MutableList<ChatMessageModel>): MutableList<ChatMessageModel> {
        if(chatsListingLiveData.value == null) {
            chatsListingLiveData.value = ArrayList()
        }
        if(chatsListingLiveData.value!!.isEmpty()) {
            return receivedMessages
        }
        val newList: MutableList<ChatMessageModel> = ArrayList<ChatMessageModel>()
        for(receivedMessage in receivedMessages) {
            var alreadyExists = false
            for(existingMessage in chatsListingLiveData.value!!) {
                if(receivedMessage.id == existingMessage.id) {
                    alreadyExists = true
                    break
                }
            }
            if(!alreadyExists) {
                newList.add(receivedMessage)
            }
        }
        return newList
    }

    fun onPullToRefresh() {
        if (shouldHitPagination) {
            isPullToRefreshLoading.value = true
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

                            chatsList.reverse()
                            chatsListingLiveData.value?.addAll(0, chatsList)

                            chatsListingLiveData.value = chatsListingLiveData.value

                            if (chatsList.size < Constants.PAGE_SIZE) {
                                shouldHitPagination = false
                            }

                            if (chatsList.size > 0) {
                                chatRoomLiveData.value?.page = chatRoomLiveData.value?.page!! + 1
                            }
                        }
                    } else {
                        baseResponse.value = it
                    }
                    isPullToRefreshLoading.value = false
                }

                val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
                chatRoomLiveData.value?.let {
                    apiResponse =
                        ChatsRepository.getMessages(
                            strToken,
                            it.recipientId!!,
                            chatRoomLiveData.value?.page!!,
                            it.pageSize
                        )
                }
                apiResponse.observeForever(observeResponse)
            } else {
                showNoInternet.value = true
                isPullToRefreshLoading.value = false
            }
        } else {
            isPullToRefreshLoading.value = false
        }
    }

    /*fun onMessageTextChanged(charSequence: CharSequence) {
        chatMessageModel.content = charSequence.toString()
    }*/

    fun onSendMessage(view: View, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            chatMessageModel.recipientId = chatRoomLiveData.value?.recipientId
            chatMessageModel.messageType = MessageTypes.TEXT
            if(messageToBeSent.value != null && messageToBeSent.value!!.isNotEmpty()) {
                chatMessageModel.content = messageToBeSent.value
                sendMessage()
            }
            /*if (!chatMessageModel.content.isNullOrEmpty()) {
                sendMessage()
            }*/
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
                    messageToBeSent.value = ""
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

    fun imagePickerClicked(view: View) {
        this.view = view
        openImagePicker.value = true
    }

    fun setImageUri(uri: Uri) {
        uploadImage(uri)
    }

    private fun uploadImage(uri: Uri) {
        if(validateInternet(view.context)) {
            hideKeyboard(view)
            loaderVisible.value = true // show loader
            uploadImageObserveResponse = Observer<BaseResponse> {
                loaderVisible.value = false
                if(validateResponse(view.context, it)){

                }
            }

            // token
            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"

            val inputStream = view.context.contentResolver.openInputStream(uri)!!
            uploadImageApiResponse = ChatsRepository.uploadImage(inputStream, strToken)
            uploadImageApiResponse.observeForever(uploadImageObserveResponse)
        }
    }

    fun userProfilePicClicked(view: View) {
        chatRoomLiveData.value?.let {
            val intent = Intent(view.context, ReceivedReactionDetailActivity::class.java)
            intent.putExtra(Constants.CHAT_ROOM, it)
            intent.putExtra(Constants.COMING_FROM, Constants.CHAT_ROOM)
            view.context.startActivity(intent)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        if (this::apiResponse.isInitialized) {
            apiResponse.removeObserver(observeResponse)
        }
        if (this::sendMessageApiResponse.isInitialized) {
            sendMessageApiResponse.removeObserver(sendMessageObserveResponse)
        }
        if (this::uploadImageApiResponse.isInitialized) {
            uploadImageApiResponse.removeObserver(uploadImageObserveResponse)
        }
    }

    fun isMessageSent(senderId: Int): Boolean {
        return chatRoomLiveData.value?.senderId == senderId
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

    fun getIsPullToRefreshLoading(): LiveData<Boolean> {
        return isPullToRefreshLoading
    }

    fun getMessageToBeSent(): LiveData<String> {
        return messageToBeSent
    }

    fun getShouldMoveToBottom(): LiveData<Boolean> {
        return shouldMoveToBottom
    }

    fun setShouldMoveToBottom(shouldMove: Boolean) {
        shouldMoveToBottom.value = shouldMove
    }

    fun getOpenImagePicker(): LiveData<Boolean> {
        return openImagePicker
    }

    fun setOpenImagePicker(open: Boolean) {
        openImagePicker.value = open
    }
}