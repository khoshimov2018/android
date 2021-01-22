package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dating.interfaces.IInterestClick
import com.example.dating.models.InterestModel
import com.example.dating.models.UserModel

class UserProfileViewModel : BaseViewModel(), IInterestClick {

    private val userProfileLiveData: MutableLiveData<UserModel> = MutableLiveData()

    fun getInterestsList(): MutableList<InterestModel> {
        val list = ArrayList<InterestModel>()
        if(userProfileLiveData.value != null && userProfileLiveData.value?.interestLabels != null) {
            for(interestLabel in userProfileLiveData.value!!.interestLabels!!) {
                list.add(InterestModel(interestLabel, true))
            }
        }
        return list
    }

    override fun interestItemClicked(view: View, interestItem: InterestModel) {
        // Do nothing
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