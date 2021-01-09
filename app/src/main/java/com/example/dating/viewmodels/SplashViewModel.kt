package com.example.dating.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dating.utils.Constants
import com.example.dating.utils.SharedPreferenceHelper

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val shouldMoveToHome: MutableLiveData<Boolean> = MutableLiveData()
    private val shouldMoveToRegistrationFlow: MutableLiveData<Boolean> = MutableLiveData()

    fun checkIfUserLoggedIn() {
        val isUserLoggedIn = SharedPreferenceHelper.getBooleanFromShared(getApplication<Application>().applicationContext, Constants.IS_USER_LOGGED_IN)
        if(isUserLoggedIn) {
            shouldMoveToHome.value = true
        } else {
            val isRegistrationDone = SharedPreferenceHelper.getBooleanFromShared(getApplication<Application>().applicationContext, Constants.IS_REGISTRATION_DONE)
            if(isRegistrationDone) {
                shouldMoveToRegistrationFlow.value = true
            } else {
                shouldMoveToHome.value = false
            }
        }
    }

    fun getShouldMoveToHome(): LiveData<Boolean> {
        return shouldMoveToHome
    }

    fun getShouldMoveToRegistrationFlow(): LiveData<Boolean> {
        return shouldMoveToRegistrationFlow
    }
}