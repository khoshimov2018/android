package ru.behetem.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.behetem.utils.Constants
import ru.behetem.utils.openUrlInBrowser

class ResetPasswordViewModel: BaseViewModel() {

    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    fun onResetClicked(view: View) {

    }

    fun onPrivacyClicked(view: View) {
        openUrlInBrowser(view.context, Constants.PRIVACY_URL)
    }

    fun onNewPasswordTextChanged(charSequence: CharSequence) {
//        userModelLiveData.value?.name = charSequence.toString()
        errorResId.value = null
    }

    fun onConfirmPasswordTextChanged(charSequence: CharSequence) {
//        userModelLiveData.value?.name = charSequence.toString()
        errorResId.value = null
    }

    fun getErrorResId(): LiveData<Int> {
        return errorResId
    }
}