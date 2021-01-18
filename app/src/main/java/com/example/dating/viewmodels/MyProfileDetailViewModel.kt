package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dating.models.UserModel

class MyProfileDetailViewModel: BaseViewModel() {

    private val userProfileLiveData: MutableLiveData<UserModel> = MutableLiveData()
    private val moveToEditProfile: MutableLiveData<Boolean> = MutableLiveData()

    fun editProfileClicked(view: View) {
        moveToEditProfile.value = true
    }

    fun getMoveToEditProfile(): LiveData<Boolean> {
        return moveToEditProfile
    }

    fun setMoveToEditProfile(move: Boolean) {
        moveToEditProfile.value = move
    }

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