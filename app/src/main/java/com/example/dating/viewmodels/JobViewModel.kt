package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.dating.models.UserModel
import com.example.dating.models.WorkInfoModel

class JobViewModel: BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private lateinit var apiResponse: LiveData<UserModel>
    private lateinit var observeResponse: Observer<UserModel>

    private val workInfoModel = WorkInfoModel()

    override fun moveFurther(view: View) {
        userModelLiveData.value?.let {
            it.workInfo = workInfoModel
        }
        moveFurther.value = true
    }

    fun onPositionTextChanged(charSequence: CharSequence) {
        workInfoModel.position = charSequence.toString()
    }

    fun onCompanyTextChanged(charSequence: CharSequence) {
        workInfoModel.companyName = charSequence.toString()
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