package com.example.dating.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.dating.models.UserModel

class EducationViewModel: BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private lateinit var apiResponse: LiveData<UserModel>
    private lateinit var observeResponse: Observer<UserModel>

    fun onUsernameTextChanged(charSequence: CharSequence) {
//        user.username = charSequence.toString()
//        errorResId.value = null
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
}