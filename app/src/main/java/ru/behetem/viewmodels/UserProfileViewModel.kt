package ru.behetem.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ru.behetem.interfaces.IInterestClick
import ru.behetem.models.InterestModel
import ru.behetem.models.ReactionModel
import ru.behetem.models.UserModel
import ru.behetem.repositories.FiltersRepository
import ru.behetem.repositories.UserRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.Constants
import ru.behetem.utils.SharedPreferenceHelper
import ru.behetem.utils.validateInternet
import ru.behetem.utils.validateResponse

class UserProfileViewModel : BaseViewModel(), IInterestClick {

    private val userProfileLiveData: MutableLiveData<UserModel> = MutableLiveData()
    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>
    private val moveToNextProfile: MutableLiveData<Boolean> = MutableLiveData()

    fun getInterestsList(): MutableList<InterestModel> {
        val list = ArrayList<InterestModel>()
        if (userProfileLiveData.value != null && userProfileLiveData.value?.interestLabels != null) {
            for (interestLabel in userProfileLiveData.value!!.interestLabels!!) {
                list.add(InterestModel(interestLabel, true))
            }
        }
        return list
    }

    fun onReactionClicked(view: View, reactionItem: ReactionModel) {
        if (validateInternet(view.context)) {
            loaderVisible.value = true // show loader
            observeResponse = Observer<BaseResponse> { response ->
                loaderVisible.value = false
                if (validateResponse(view.context, response)) {
                    moveToNextProfile.value = true
                }
            }
            // token
            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"

            val reactionModel = ReactionModel()
            reactionModel.reaction = reactionItem.id
            reactionModel.receiverId = userProfileLiveData.value?.id
            apiResponse = UserRepository.sendReaction(reactionModel, strToken)
            apiResponse.observeForever(observeResponse)
        }
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

    fun getReactionsList(): MutableList<ReactionModel>? {
        return userProfileLiveData.value?.reactions
    }

    fun getUserProfileLiveData(): LiveData<UserModel> {
        return userProfileLiveData
    }

    fun setCurrentUser(userModel: UserModel) {
        userProfileLiveData.value = userModel
    }

    fun getCurrentUser(): UserModel? {
        return userProfileLiveData.value
    }

    fun getMoveToNextProfile(): LiveData<Boolean> {
        return moveToNextProfile
    }

    fun setMoveToNextProfile(move: Boolean) {
        moveToNextProfile.value = move
    }
}