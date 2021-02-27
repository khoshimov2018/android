package ru.behetem.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.behetem.interfaces.IReactionClick
import ru.behetem.models.InterestModel
import ru.behetem.models.ReactionModel
import ru.behetem.repositories.InterestsRepository
import ru.behetem.repositories.ReactionsRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.ApiConstants
import ru.behetem.utils.isInternetAvailable
import ru.behetem.utils.validateResponseWithoutPopup

class MessengerViewModel(application: Application) : BaseAndroidViewModel(application), IReactionClick {

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>
    private val baseResponse: MutableLiveData<BaseResponse> = MutableLiveData()
    private val receivedReactionsList: MutableLiveData<MutableList<ReactionModel>> = MutableLiveData()
    private val allClicked: MutableLiveData<Boolean> = MutableLiveData()

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

    override fun reactionItemClicked(view: View, reactionItem: ReactionModel) {

    }

    fun onAllClicked(view: View) {
        allClicked.value = true
    }

    override fun onCleared() {
        super.onCleared()
        if (this::apiResponse.isInitialized) {
            apiResponse.removeObserver(observeResponse)
        }
    }

    fun getBaseResponse(): LiveData<BaseResponse?> {
        return baseResponse
    }

    fun setBaseResponse(baseResponse: BaseResponse?) {
        this.baseResponse.value = baseResponse
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
}