package ru.behetem.viewmodels

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.behetem.activities.ChatActivity
import ru.behetem.activities.ReceivedReactionDetailActivity
import ru.behetem.interfaces.IReactionClick
import ru.behetem.models.ChatRoomModel
import ru.behetem.models.InterestModel
import ru.behetem.models.NationalityModel
import ru.behetem.models.ReactionModel
import ru.behetem.repositories.ChatsRepository
import ru.behetem.repositories.InterestsRepository
import ru.behetem.repositories.ReactionsRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.ApiConstants
import ru.behetem.utils.Constants
import ru.behetem.utils.isInternetAvailable
import ru.behetem.utils.validateResponseWithoutPopup

class MessengerViewModel(application: Application) : BaseAndroidViewModel(application), IReactionClick {

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>
    private lateinit var chatRoomsApiResponse: LiveData<BaseResponse>
    private lateinit var chatRoomsObserveResponse: Observer<BaseResponse>
    private val receivedReactionsList: MutableLiveData<MutableList<ReactionModel>> = MutableLiveData()
    private val allClicked: MutableLiveData<Boolean> = MutableLiveData()
    private val chatRoomsLiveData: MutableLiveData<MutableList<ChatRoomModel>> = MutableLiveData()

    fun getReactions() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            observeResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
                    if (it.data is MutableList<*>) {
                        val gson = Gson()
                        val strResponse = gson.toJson(it.data)
                        val myType = object : TypeToken<MutableList<ReactionModel>>() {}.type
                        val reactionsList: MutableList<ReactionModel> =
                            gson.fromJson<MutableList<ReactionModel>>(strResponse, myType)

                        for (reaction in reactionsList) {
                            reaction.image = "${ApiConstants.BASE_URL}${reaction.image}"
                        }

                        receivedReactionsList.value = reactionsList
                    }
                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            apiResponse = ReactionsRepository.getReactions(strToken)
            apiResponse.observeForever(observeResponse)
        } else {
            showNoInternet.value = true
        }
    }

    fun getChatRooms() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            chatRoomsObserveResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
                    if (it.data is MutableList<*>) {
                        val gson = Gson()
                        val strResponse = gson.toJson(it.data)
                        val myType = object : TypeToken<MutableList<ChatRoomModel>>() {}.type
                        val chatRooms: MutableList<ChatRoomModel> = gson.fromJson<MutableList<ChatRoomModel>>(strResponse, myType)

                        for(chatRoom in chatRooms) {
                            chatRoom.receiverImage = "${ApiConstants.BASE_URL}${chatRoom.receiverImage}"
                        }

                        chatRoomsLiveData.value = chatRooms
                    }
                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            chatRoomsApiResponse = ChatsRepository.getChatRooms(strToken)
            chatRoomsApiResponse.observeForever(chatRoomsObserveResponse)
        } else {
            showNoInternet.value = true
        }
    }

    fun deleteChatRoom(position: Int) {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            chatRoomsObserveResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
                    chatRoomsLiveData.value?.removeAt(position)
                    chatRoomsLiveData.value = chatRoomsLiveData.value
                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            val chatRoomModel = ChatRoomModel()
            chatRoomModel.chatId = chatRoomsLiveData.value?.get(position)?.chatId
            chatRoomsApiResponse = ChatsRepository.deleteChatRoom(strToken, chatRoomModel)
            chatRoomsApiResponse.observeForever(chatRoomsObserveResponse)
        } else {
            showNoInternet.value = true
        }
    }

    override fun reactionItemClicked(view: View, reactionItem: ReactionModel) {
        val intent = Intent(view.context, ReceivedReactionDetailActivity::class.java)
        intent.putExtra(Constants.RECEIVED_REACTION, reactionItem)
        view.context.startActivity(intent)
    }

    fun onAllClicked(view: View) {
        allClicked.value = true
    }

    fun chatRoomClicked(view: View, chatRoomItem: ChatRoomModel) {
        val intent = Intent(view.context, ChatActivity::class.java)
        intent.putExtra(Constants.CHAT_ROOM, chatRoomItem)
        view.context.startActivity(intent)
    }

    override fun onCleared() {
        super.onCleared()
        if (this::apiResponse.isInitialized) {
            apiResponse.removeObserver(observeResponse)
        }
        if (this::chatRoomsApiResponse.isInitialized) {
            chatRoomsApiResponse.removeObserver(chatRoomsObserveResponse)
        }
    }

    fun getReceivedReactionsList(): LiveData<MutableList<ReactionModel>> {
        return receivedReactionsList
    }

    fun getAllClicked(): LiveData<Boolean> {
        return allClicked
    }

    fun setAllClicked(clicked: Boolean) {
        allClicked.value = clicked
    }

    fun getChatRoomsLiveData(): LiveData<MutableList<ChatRoomModel>> {
        return chatRoomsLiveData
    }
}