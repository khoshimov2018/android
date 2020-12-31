package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MyProfileDetailViewModel: BaseViewModel() {

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
}