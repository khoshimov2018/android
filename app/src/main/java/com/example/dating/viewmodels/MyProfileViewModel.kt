package com.example.dating.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.dating.models.UserModel
import com.example.dating.repositories.UserRepository
import com.example.dating.utils.isInternetAvailable
import com.example.dating.utils.validateResponse
import com.example.dating.utils.validateResponseWithoutPopup

class MyProfileViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val moveToSettings: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToProfile: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToCoins: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToPremium: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var apiResponse: LiveData<UserModel>
    private lateinit var observeResponse: Observer<UserModel>

    private val baseResponse: MutableLiveData<UserModel> = MutableLiveData()

    fun getUserProfile() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            observeResponse = Observer<UserModel> {
                loaderVisible.value = false
                if(validateResponseWithoutPopup(it)) {

                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            apiResponse = UserRepository.getInfo(strToken)
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

    fun getBaseResponse(): LiveData<UserModel?> {
        return baseResponse
    }

    fun setBaseResponse(baseResponse: UserModel?) {
        this.baseResponse.value = baseResponse
    }
}