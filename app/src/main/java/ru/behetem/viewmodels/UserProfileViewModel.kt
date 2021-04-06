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
import ru.behetem.repositories.ReactionsRepository
import ru.behetem.repositories.UserRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.*

class UserProfileViewModel : BaseViewModel(), IInterestClick {

    private val userProfileLiveData: MutableLiveData<UserModel> = MutableLiveData()
    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>
    private val moveToNextProfile: MutableLiveData<Boolean> = MutableLiveData()
    private val showOutOfReactionsPopup: MutableLiveData<Boolean> = MutableLiveData()

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

    fun onReactionClicked(view: View, reactionItem: ReactionModel) {
        val commercialModel = getCommercialFromShared(view.context)
        var allow = false
        var left: Int? = null
        if(reactionItem.type == ReactionType.SUPER) {
            left = commercialModel?.actionsLeft?.SUPER_REACTION
            if(left != null && left > 0) {
                allow = true
            }
        } else {
            left = commercialModel?.actionsLeft?.STANDARD_REACTION
            if(left != null && left > 0) {
                allow = true
            }
        }
        if(allow) {
            if (validateInternet(view.context)) {
                loaderVisible.value = true // show loader
                observeResponse = Observer<BaseResponse> { response ->
                    loaderVisible.value = false
                    if (validateResponse(view.context, response)) {
                        if(left != null) {
                            left -= 1
                        }

                        if(reactionItem.type == ReactionType.SUPER) {
                            commercialModel?.actionsLeft?.SUPER_REACTION = left
                        } else {
                            commercialModel?.actionsLeft?.STANDARD_REACTION = left
                        }

                        commercialModel?.let {
                            saveCommercialToShared(view.context, it)
                        }

                        moveToNextProfile.value = true
                    }
                }
                // token
                val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"

                val reactionModel = ReactionModel()
                reactionModel.reaction = reactionItem.id
                reactionModel.receiverId = userProfileLiveData.value?.id
                apiResponse = ReactionsRepository.sendReaction(reactionModel, strToken)
                apiResponse.observeForever(observeResponse)
            }
        } else {
            showOutOfReactionsPopup.value = true
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

    fun getShowOutOfReactionsPopup(): LiveData<Boolean> {
        return showOutOfReactionsPopup
    }

    fun setShowOutOfReactionsPopup(show: Boolean) {
        showOutOfReactionsPopup.value = show
    }
}