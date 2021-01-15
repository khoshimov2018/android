package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.dating.models.EducationInfoModel
import com.example.dating.models.UserModel

class EducationViewModel: BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private lateinit var apiResponse: LiveData<UserModel>
    private lateinit var observeResponse: Observer<UserModel>

    private val educationInfoModel = EducationInfoModel()

    override fun moveFurther(view: View) {
        userModelLiveData.value?.let {
            it.educationInfo = educationInfoModel
        }
        moveFurther.value = true
    }

    fun onInstitutionNameTextChanged(charSequence: CharSequence) {
        educationInfoModel.institutionName = charSequence.toString()
    }

    fun onLevelTextChanged(charSequence: CharSequence) {
        educationInfoModel.level = charSequence.toString()
    }

    fun onGraduationYearTextChanged(charSequence: CharSequence) {
        try {
            educationInfoModel.graduationYear = charSequence.toString().toInt()
        } catch (e: Exception) {
            educationInfoModel.graduationYear = 0
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
}