package com.example.dating.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dating.models.UserModel

open class BaseAndroidViewModel(application: Application): AndroidViewModel(application) {

    val context = application.applicationContext
    private lateinit var loggedInUser: UserModel
    val showNoInternet: MutableLiveData<Boolean> = MutableLiveData()
//    private val baseResponse: MutableLiveData<BaseResponse?> = MutableLiveData()

    private val backButtonClicked: MutableLiveData<Boolean> = MutableLiveData()
    val loaderVisible: MutableLiveData<Boolean> = MutableLiveData()

    fun backPressed(view: View) {
        backButtonClicked.value = true
    }

    fun getBackButtonClicked(): LiveData<Boolean> {
        return backButtonClicked
    }

    fun getLoaderVisible(): LiveData<Boolean> {
        return loaderVisible
    }

    open fun setLoggedInUser(user: UserModel) {
        loggedInUser = user
    }

    fun getLoggedInUser(): UserModel? {
        return if (this::loggedInUser.isInitialized)
            loggedInUser
        else null
    }

    fun getShowNoInternet(): LiveData<Boolean> {
        return showNoInternet
    }

    fun setShowNoInternet(showNoInternet: Boolean) {
        this.showNoInternet.value = showNoInternet
    }

    /*fun getBaseResponse(): LiveData<BaseResponse?> {
        return baseResponse
    }

    fun setBaseResponse(baseResponse: BaseResponse?) {
        this.baseResponse.value = baseResponse
    }*/
}