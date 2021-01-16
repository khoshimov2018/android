package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.dating.R
import com.example.dating.models.EducationInfoModel
import com.example.dating.models.UserModel
import com.example.dating.utils.EducationFormErrorConstants

class EducationViewModel: BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private lateinit var apiResponse: LiveData<UserModel>
    private lateinit var observeResponse: Observer<UserModel>
    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    private val educationInfoModel = EducationInfoModel()

    override fun moveFurther(view: View) {
        when(educationInfoModel.isEducationValid()) {
            EducationFormErrorConstants.INSTITUTE_NAME_EMPTY -> {
                errorResId.value = R.string.enter_institute_name
            }
            EducationFormErrorConstants.LEVEL_EMPTY -> {
                errorResId.value = R.string.enter_level
            }
            EducationFormErrorConstants.YEAR_EMPTY -> {
                errorResId.value = R.string.enter_graduation_year
            }
            EducationFormErrorConstants.YEAR_INVALID -> {
                errorResId.value = R.string.invalid_graduation_year
            }
            else -> {
                userModelLiveData.value?.let {
                    it.educationInfo = educationInfoModel
                }
                moveFurther.value = true
            }
        }
    }

    fun onSkipClicked(view: View) {
        moveFurther.value = true
    }

    fun onInstitutionNameTextChanged(charSequence: CharSequence) {
        educationInfoModel.institutionName = charSequence.toString()
        errorResId.value = null
    }

    fun onLevelTextChanged(charSequence: CharSequence) {
        educationInfoModel.level = charSequence.toString()
        errorResId.value = null
    }

    fun onGraduationYearTextChanged(charSequence: CharSequence) {
        try {
            educationInfoModel.graduationYear = charSequence.toString().toInt()
        } catch (e: Exception) {
            educationInfoModel.graduationYear = 0
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