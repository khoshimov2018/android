package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dating.utils.Constants
import com.example.dating.utils.openUrlInBrowser

class ForgotPasswordViewModel : BaseViewModel() {

    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    fun onForgotPasswordClicked(view: View) {

    }

    fun onPrivacyClicked(view: View) {
        openUrlInBrowser(view.context, Constants.PRIVACY_URL)
    }

    fun onEmailTextChanged(charSequence: CharSequence) {
//        userModelLiveData.value?.name = charSequence.toString()
        errorResId.value = null
    }

    fun getErrorResId(): LiveData<Int> {
        return errorResId
    }
}