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
import ru.behetem.models.InterestModel
import ru.behetem.models.NationalityModel
import ru.behetem.models.ReactionModel
import ru.behetem.models.UserModel
import ru.behetem.repositories.NationalitiesRepository
import ru.behetem.repositories.ReactionsRepository
import ru.behetem.repositories.UserRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.*

class ReceivedReactionDetailViewModel(application: Application) : BaseAndroidViewModel(application),
    IInterestClick {

    private var receivedReaction: ReactionModel? = null
    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>
    private val userProfileLiveData: MutableLiveData<UserModel> = MutableLiveData()
    private var isMutual = false
    private val moveToMessage: MutableLiveData<Boolean> = MutableLiveData()
    private val moveBack: MutableLiveData<Boolean> = MutableLiveData()

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
            receivedReaction?.senderId?.let {
                apiResponse = UserRepository.getUserDetail(strToken, it)
            }
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

    private fun updateStatus(view: View, reactionModel: ReactionModel) {
        if (validateInternet(view.context)) {
            hideKeyboard(view)
            loaderVisible.value = true // show loader
            observeResponse = Observer<BaseResponse> {
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
            apiResponse = ReactionsRepository.changeReactions(strToken, reactionModel)
            apiResponse.observeForever(observeResponse)
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
    }

    fun setReceivedReaction(reaction: ReactionModel) {
        receivedReaction = reaction
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
}