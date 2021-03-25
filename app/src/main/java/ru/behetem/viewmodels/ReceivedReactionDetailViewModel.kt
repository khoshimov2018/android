package ru.behetem.viewmodels

import android.app.Application
import android.content.DialogInterface
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.behetem.R
import ru.behetem.interfaces.IInterestClick
import ru.behetem.models.*
import ru.behetem.repositories.NationalitiesRepository
import ru.behetem.repositories.ReactionsRepository
import ru.behetem.repositories.UserRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.*

class ReceivedReactionDetailViewModel(application: Application) : BaseAndroidViewModel(application),
    IInterestClick {

    private var receivedReaction: ReactionModel? = null
    private var chatRoom: ChatRoomModel? = null
    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>
    private val userProfileLiveData: MutableLiveData<UserModel> = MutableLiveData()
    private var isMutual = false
    private val moveToMessage: MutableLiveData<Boolean> = MutableLiveData()
    private val moveBack: MutableLiveData<Boolean> = MutableLiveData()
    private lateinit var updateStatusApiResponse: LiveData<BaseResponse>
    private lateinit var updateStatusObserveResponse: Observer<BaseResponse>
    private lateinit var activityCheckApiResponse: LiveData<BaseResponse>
    private lateinit var activityCheckObserveResponse: Observer<BaseResponse>
    private val showActivityPopup: MutableLiveData<Boolean> = MutableLiveData()
    private var activityStatus: Double = -1.0
    private val showOutOfReactionsPopup: MutableLiveData<Boolean> = MutableLiveData()

    fun getUserDetail() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            observeResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
                    val gson = Gson()
                    val strResponse = gson.toJson(it.data)
                    val myType = object : TypeToken<UserModel>() {}.type
                    val user: UserModel = gson.fromJson<UserModel>(strResponse, myType)

                    val imagesList = ArrayList<String>()
                    if (user.images != null) {
                        for (image in user.images!!) {
                            val imageUrl = "${ApiConstants.BASE_URL}$image"
                            imagesList.add(imageUrl)
                        }
                        user.images!!.clear()
                        user.images!!.addAll(imagesList)
                    }

                    userProfileLiveData.value = user
                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"

            apiResponse = UserRepository.getUserDetail(strToken, getUserId())
            apiResponse.observeForever(observeResponse)
        } else {
            showNoInternet.value = true
        }
    }

    fun onWriteMessageClicked(view: View) {
        val reactionModel = ReactionModel()
        reactionModel.reactionId = receivedReaction?.id
        reactionModel.status = ReactionStatuses.MUTUAL
        isMutual = true
        updateStatus(view, reactionModel)
    }

    fun onSkipClicked(view: View) {
        val context = view.context
        showAlertDialog(
            context,
            null,
            context.getString(R.string.sure_skip_user),
            context.getString(R.string.yes),
            DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.cancel()
                val reactionModel = ReactionModel()
                reactionModel.reactionId = receivedReaction?.id
                reactionModel.status = ReactionStatuses.IGNORED
                isMutual = false
                updateStatus(view, reactionModel)
            },
            context.getString(R.string.no),
            null
        )
    }

    fun onCheckActivityClicked(view: View) {
        val commercialModel = getCommercialFromShared(view.context)
        var left: Int? = commercialModel?.actionsLeft?.ACTIVITY_CHECK
        var allow = left != null && left > 0

        if(allow) {
            if (validateInternet(view.context)) {
                hideKeyboard(view)
                loaderVisible.value = true // show loader
                activityCheckObserveResponse = Observer<BaseResponse> {
                    loaderVisible.value = false
                    if (validateResponse(view.context, it)) {
                        if(left != null) {
                            left -= 1
                        }
                        commercialModel?.actionsLeft?.ACTIVITY_CHECK = left
                        commercialModel?.let {
                            saveCommercialToShared(view.context, it)
                        }

                        activityStatus = it.data as Double
                        showActivityPopup.value = true
                    }
                }

                val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
                activityCheckApiResponse = UserRepository.activityCheck(strToken, getUserId())
                activityCheckApiResponse.observeForever(activityCheckObserveResponse)
            }
        } else {
            showOutOfReactionsPopup.value = true
        }
    }

    private fun updateStatus(view: View, reactionModel: ReactionModel) {
        if (validateInternet(view.context)) {
            hideKeyboard(view)
            loaderVisible.value = true // show loader
            updateStatusObserveResponse = Observer<BaseResponse> {
                loaderVisible.value = false
                if (validateResponse(view.context, it)) {
                    if (isMutual) {
                        moveToMessage.value = true
                    } else {
                        moveBack.value = true
                    }
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            updateStatusApiResponse = ReactionsRepository.changeReactions(strToken, reactionModel)
            updateStatusApiResponse.observeForever(updateStatusObserveResponse)
        }
    }

    fun getInterestsList(): MutableList<InterestModel> {
        val list = ArrayList<InterestModel>()
        if (userProfileLiveData.value != null && userProfileLiveData.value?.interests != null) {
            for (interestLabel in userProfileLiveData.value!!.interests!!) {
                val interest = InterestModel()
                interest.label = interestLabel
                interest.isSelected = true
                list.add(interest)
            }
        }
        return list
    }

    override fun interestItemClicked(view: View, interestItem: InterestModel) {
        // Do nothing
    }

    override fun onCleared() {
        super.onCleared()
        if (this::apiResponse.isInitialized) {
            apiResponse.removeObserver(observeResponse)
        }
        if (this::activityCheckApiResponse.isInitialized) {
            activityCheckApiResponse.removeObserver(activityCheckObserveResponse)
        }
        if (this::updateStatusApiResponse.isInitialized) {
            updateStatusApiResponse.removeObserver(updateStatusObserveResponse)
        }
    }

    private fun getUserId(): Int {
        return if(receivedReaction != null && receivedReaction!!.senderId != null) {
            receivedReaction!!.senderId!!
        } else if(chatRoom != null && chatRoom!!.recipientId != null) {
            chatRoom!!.recipientId!!.toInt()
        } else {
            0
        }
    }

    fun isReceivedReaction(): Boolean {
        return receivedReaction != null
    }

    fun isChatUser(): Boolean {
        return chatRoom != null
    }

    fun setReceivedReaction(reaction: ReactionModel) {
        receivedReaction = reaction
    }

    fun setChatRoom(chatRoom: ChatRoomModel) {
        this.chatRoom = chatRoom
    }

    fun getUserProfileLiveData(): LiveData<UserModel> {
        return userProfileLiveData
    }

    fun getMoveToMessage(): LiveData<Boolean> {
        return moveToMessage
    }

    fun setMoveToMessage(move: Boolean) {
        moveToMessage.value = move
    }

    fun getMoveBack(): LiveData<Boolean> {
        return moveBack
    }

    fun setMoveBack(move: Boolean) {
        moveBack.value = move
    }

    fun getShowActivityPopup(): LiveData<Boolean> {
        return showActivityPopup
    }

    fun setShowActivityPopup(show: Boolean) {
        showActivityPopup.value = show
    }

    fun getActivityStatus(): Double {
        return activityStatus
    }

    fun getShowOutOfReactionsPopup(): LiveData<Boolean> {
        return showOutOfReactionsPopup
    }

    fun setShowOutOfReactionsPopup(show: Boolean) {
        showOutOfReactionsPopup.value = show
    }
}