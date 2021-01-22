package com.example.dating.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dating.models.UserModel

class UserProfileViewModel : BaseViewModel() {

    private val userProfileLiveData: MutableLiveData<UserModel> = MutableLiveData()

    fun getUserProfileLiveData(): LiveData<UserModel> {
        return userProfileLiveData
    }

    fun setCurrentUser(userModel: UserModel) {
        userProfileLiveData.value = userModel
    }

    fun getCurrentUser(): UserModel? {
        return userProfileLiveData.value
    }
}