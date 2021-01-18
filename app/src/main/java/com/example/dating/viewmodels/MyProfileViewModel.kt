package com.example.dating.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.dating.R
import com.example.dating.models.UserModel
import com.example.dating.repositories.UserRepository
import com.example.dating.responses.BaseResponse
import com.example.dating.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyProfileViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val moveToSettings: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToProfile: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToCoins: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToPremium: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>

    private val baseResponse: MutableLiveData<BaseResponse> = MutableLiveData()
    private val userProfileLiveData: MutableLiveData<UserModel> = MutableLiveData()

    fun getUserProfile() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            observeResponse = Observer<BaseResponse> {
                loaderVisible.value = false
                if (validateResponseWithoutPopup(it)) {
                    val gson = Gson()
                    val strResponse = gson.toJson(it.data)
                    val myType = object : TypeToken<UserModel>() {}.type
                    val responseUser: UserModel = gson.fromJson<UserModel>(strResponse, myType)

                    userProfileLiveData.value = responseUser
                } else {
                    baseResponse.value = it
                }

                getCurrentUserImages()
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            apiResponse = UserRepository.getInfo(strToken)
            apiResponse.observeForever(observeResponse)
        } else {
            showNoInternet.value = true
        }
    }

    private fun getCurrentUserImages() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            observeResponse = Observer<BaseResponse> {
                loaderVisible.value = false
                if (validateResponseWithoutPopup(it)) {

                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            apiResponse = UserRepository.getCurrentUserImages(strToken)
            apiResponse.observeForever(observeResponse)
        } else {
            showNoInternet.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (this::apiResponse.isInitialized) {
            apiResponse.removeObserver(observeResponse)
        }
    }

    fun settingsClicked(view: View) {
        moveToSettings.value = true
    }

    fun profileClicked(view: View) {
        moveToProfile.value = true
    }

    fun coinsClicked(view: View) {
        moveToCoins.value = true
    }

    fun premiumClicked(view: View) {
        moveToPremium.value = true
    }

    fun getMoveToSettings(): LiveData<Boolean> {
        return moveToSettings
    }

    fun setMoveToSettings(move: Boolean) {
        moveToSettings.value = move
    }

    fun getMoveToProfile(): LiveData<Boolean> {
        return moveToProfile
    }

    fun setMoveToProfile(move: Boolean) {
        moveToProfile.value = move
    }

    fun getMoveToCoins(): LiveData<Boolean> {
        return moveToCoins
    }

    fun setMoveToCoins(move: Boolean) {
        moveToCoins.value = move
    }

    fun getMoveToPremium(): LiveData<Boolean> {
        return moveToPremium
    }

    fun setMoveToPremium(move: Boolean) {
        moveToPremium.value = move
    }

    fun getBaseResponse(): LiveData<BaseResponse?> {
        return baseResponse
    }

    fun setBaseResponse(baseResponse: BaseResponse?) {
        this.baseResponse.value = baseResponse
    }

    fun getUserProfileLiveData(): LiveData<UserModel> {
        return userProfileLiveData
    }

    fun getCurrentUser(): UserModel? {
        return userProfileLiveData.value
    }
}