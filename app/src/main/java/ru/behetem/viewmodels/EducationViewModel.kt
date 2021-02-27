package ru.behetem.viewmodels

import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ru.behetem.R
import ru.behetem.models.CareerInfoModel
import ru.behetem.models.EducationInfoModel
import ru.behetem.models.UserModel
import ru.behetem.utils.EducationFormErrorConstants
import ru.behetem.utils.EducationLevels
import ru.behetem.utils.WorkInfoFormErrorConstants

class EducationViewModel: BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private val errorResId: MutableLiveData<Int> = MutableLiveData()
    private val jobErrorResId: MutableLiveData<Int> = MutableLiveData()

    private val careerInfoModel = CareerInfoModel()

    override fun moveFurther(view: View) {
        when(careerInfoModel.isEducationValid()) {
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
            EducationFormErrorConstants.POSITION_EMPTY -> {
                jobErrorResId.value = R.string.enter_position
            }
            EducationFormErrorConstants.COMPANY_NAME_EMPTY -> {
                jobErrorResId.value = R.string.enter_company_name
            }
            else -> {
                userModelLiveData.value?.let {
                    it.careerInfo = careerInfoModel
                }
                moveFurther.value = true
            }
        }
    }

    fun onSkipClicked(view: View) {
        moveFurther.value = true
    }

    fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                careerInfoModel.educationLevel = null
            }
            1 -> {
                careerInfoModel.educationLevel = EducationLevels.GENERAL
            }
            2 -> {
                careerInfoModel.educationLevel = EducationLevels.SECONDARY
            }
            3 -> {
                careerInfoModel.educationLevel = EducationLevels.SPECIALIZED_SECONDARY
            }
            4 -> {
                careerInfoModel.educationLevel = EducationLevels.INCOMPLETE_HIGHER
            }
            else -> {
                careerInfoModel.educationLevel = EducationLevels.HIGHER
            }
        }

        errorResId.value = null
    }

    fun onInstitutionNameTextChanged(charSequence: CharSequence) {
        careerInfoModel.institutionName = charSequence.toString()
        errorResId.value = null
    }

    fun onGraduationYearTextChanged(charSequence: CharSequence) {
        try {
            careerInfoModel.graduationYear = charSequence.toString().toInt()
        } catch (e: Exception) {
            careerInfoModel.graduationYear = 0
        }
        errorResId.value = null
    }

    fun onPositionTextChanged(charSequence: CharSequence) {
        careerInfoModel.workPosition = charSequence.toString()
        jobErrorResId.value = null
    }

    fun onCompanyTextChanged(charSequence: CharSequence) {
        careerInfoModel.companyName = charSequence.toString()
        jobErrorResId.value = null
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

    fun getJobErrorResId(): LiveData<Int> {
        return jobErrorResId
    }
}