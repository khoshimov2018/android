package ru.behetem.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ru.behetem.R
import ru.behetem.models.UserModel
import ru.behetem.repositories.UserRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.*
import java.util.*

class EnterDobViewModel : BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private val showDatePicker: MutableLiveData<Boolean> = MutableLiveData()
    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>

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
                    if(validateInternet(view.context)) {
//                        userModelLiveData.value?.selectedDOB = null
                        hideKeyboard(view)
                        loaderVisible.value = true // show loader
                        observeResponse = Observer<BaseResponse> { response ->
                            loaderVisible.value = false
                            if (validateResponse(view.context, response)) {
                                moveFurther.value = true
                            }
                        }
                        // token
                        val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"

                        apiResponse = UserRepository.changeInfo(userModelLiveData.value!!, strToken)
                        apiResponse.observeForever(observeResponse)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (this::apiResponse.isInitialized) {
            apiResponse.removeObserver(observeResponse)
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