package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.dating.R
import com.example.dating.models.UserModel

class AboutMeViewModel : BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private lateinit var apiResponse: LiveData<UserModel>
    private lateinit var observeResponse: Observer<UserModel>
    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    override fun moveFurther(view: View) {
        userModelLiveData.value?.let {
            if(it.description.isNullOrEmpty()) {
                errorResId.value = R.string.enter_about_me
            } else {
                moveFurther.value = true
            }
        }
    }

    fun onSkipClicked(view: View) {
        moveFurther.value = true
    }

    fun onAboutMeTextChanged(charSequence: CharSequence) {
        userModelLiveData.value?.let {
            it.description = charSequence.toString()
        }
        errorResId.value = null
    }

    fun setCurrentUser(userModel: UserModel) {
        this.userModelLiveData.value = userModel
    }

    fun getCurrentUser(): UserModel? {
        return userModelLiveData.value
    }

    fun getUserModelLiveData(): LiveData<UserModel> {
        return userModelLiveData
    }

    fun getErrorResId(): LiveData<Int> {
        return errorResId
    }
}