package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dating.R
import com.example.dating.models.UserModel
import com.example.dating.utils.Gender

class ChooseGenderViewModel : BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    fun onMaleClicked(view: View) {
        userModelLiveData.value?.gender = Gender.MALE
        userModelLiveData.value = userModelLiveData.value
        errorResId.value = null
    }

    fun onFemaleClicked(view: View) {
        userModelLiveData.value?.gender = Gender.FEMALE
        userModelLiveData.value = userModelLiveData.value
        errorResId.value = null
    }

    override fun moveFurther(view: View) {
        userModelLiveData.value?.let {
            if(it.isGenderSelected()) {
                moveFurther.value = true
            } else {
                errorResId.value = R.string.choose_gender
            }
        }
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