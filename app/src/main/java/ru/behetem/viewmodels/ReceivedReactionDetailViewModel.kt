package ru.behetem.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.behetem.interfaces.IInterestClick
import ru.behetem.models.InterestModel
import ru.behetem.models.NationalityModel
import ru.behetem.models.ReactionModel
import ru.behetem.models.UserModel
import ru.behetem.repositories.NationalitiesRepository
import ru.behetem.repositories.UserRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.ApiConstants
import ru.behetem.utils.isInternetAvailable
import ru.behetem.utils.validateResponseWithoutPopup

class ReceivedReactionDetailViewModel(application: Application) : BaseAndroidViewModel(application), IInterestClick {

    private var receivedReaction: ReactionModel? = null
    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>
    private val userProfileLiveData: MutableLiveData<UserModel> = MutableLiveData()

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
                    if(user.images != null) {
                        for(image in user.images!!) {
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

    }

    fun onSkipClicked(view: View) {

    }

    fun getInterestsList(): MutableList<InterestModel> {
        val list = ArrayList<InterestModel>()
        if(userProfileLiveData.value != null && userProfileLiveData.value?.interests != null) {
            for(interestLabel in userProfileLiveData.value!!.interests!!) {
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
}