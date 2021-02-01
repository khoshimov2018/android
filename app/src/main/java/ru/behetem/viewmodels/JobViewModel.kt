package ru.behetem.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ru.behetem.R
import ru.behetem.models.UserModel
import ru.behetem.models.WorkInfoModel
import ru.behetem.utils.WorkInfoFormErrorConstants

class JobViewModel: BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private lateinit var apiResponse: LiveData<UserModel>
    private lateinit var observeResponse: Observer<UserModel>
    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    private val workInfoModel = WorkInfoModel()

    override fun moveFurther(view: View) {
        when(workInfoModel.isWorkInfoValid()) {
            WorkInfoFormErrorConstants.POSITION_EMPTY -> {
                errorResId.value = R.string.enter_position
            }
            WorkInfoFormErrorConstants.COMPANY_NAME_EMPTY -> {
                errorResId.value = R.string.enter_company_name
            }
            else -> {
                userModelLiveData.value?.let {
                    it.workInfo = workInfoModel
                }
                moveFurther.value = true
            }
        }
    }

    fun onSkipClicked(view: View) {
        moveFurther.value = true
    }

    fun onPositionTextChanged(charSequence: CharSequence) {
        workInfoModel.position = charSequence.toString()
        errorResId.value = null
    }

    fun onCompanyTextChanged(charSequence: CharSequence) {
        workInfoModel.companyName = charSequence.toString()
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