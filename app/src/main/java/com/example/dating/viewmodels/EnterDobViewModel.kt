package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dating.R
import com.example.dating.models.UserModel
import com.example.dating.utils.DobErrorConstants
import com.example.dating.utils.formatDobForAPI
import com.example.dating.utils.printLog
import java.util.*

class EnterDobViewModel : BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private val showDatePicker: MutableLiveData<Boolean> = MutableLiveData()
    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    override fun moveFurther(view: View) {
        userModelLiveData.value?.let {
            when (it.validateDob()) {
                DobErrorConstants.DOB_EMPTY -> {
                    errorResId.value = R.string.choose_dob
                }
                DobErrorConstants.AGE_LESS -> {
                    errorResId.value = R.string.at_least_18
                }
                else -> {
                    moveFurther.value = true
                }
            }
        }
    }

    fun setDateOfBirth(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)

        userModelLiveData.value?.selectedDOB = calendar
        userModelLiveData.value?.dateOfBirth = formatDobForAPI(calendar)
        userModelLiveData.value = userModelLiveData.value
        errorResId.value = null
    }

    fun onDobClicked(view: View) {
        showDatePicker.value = true
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

    fun getShowDatePicker(): LiveData<Boolean> {
        return showDatePicker
    }

    fun setShowDatePicker(show: Boolean) {
        showDatePicker.value = show
    }
}