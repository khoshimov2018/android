package ru.behetem.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.behetem.R
import ru.behetem.models.UserModel
import ru.behetem.repositories.UserRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.LoginFormErrorConstants
import ru.behetem.utils.SharedPreferenceHelper
import ru.behetem.utils.*

class RegistrationViewModel : BaseViewModel() {

    private val userModel = UserModel()

    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>

    fun registerClicked(view: View) {
        when (userModel.validateRegistrationData()) {
            LoginFormErrorConstants.USERNAME_EMPTY -> {
                errorResId.value = R.string.enter_email
            }
            LoginFormErrorConstants.USERNAME_NOT_VALID -> {
                errorResId.value = R.string.enter_valid_email
            }
            LoginFormErrorConstants.PASSWORD_EMPTY -> {
                errorResId.value = R.string.enter_password
            }
            else -> {
                if(validateInternet(view.context)) {
                    hideKeyboard(view)
                    loaderVisible.value = true // show loader
                    observeResponse = Observer<BaseResponse> {
                        loaderVisible.value = false
                        if (validateResponse(view.context, it)) {
                            val gson = Gson()
                            val strResponse = gson.toJson(it.data)
                            val myType = object : TypeToken<UserModel>() {}.type
                            val responseUser: UserModel = gson.fromJson<UserModel>(strResponse, myType)

                            // Complete the profile first
                            SharedPreferenceHelper.saveBooleanToShared(
                                view.context,
                                Constants.IS_REGISTRATION_DONE,
                                true
                            )
                            saveLoggedInUserToShared(view.context, responseUser)
                            moveFurther.value = true
                        }
                    }
                    userModel.timezone = getTimeZone()
                    apiResponse = UserRepository.register(userModel)
                    apiResponse.observeForever(observeResponse)
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

    fun onEmailTextChanged(charSequence: CharSequence) {
        userModel.email = charSequence.toString()
        errorResId.value = null
    }

    fun onPasswordTextChanged(charSequence: CharSequence) {
        userModel.password = charSequence.toString()
        errorResId.value = null
    }

    fun getErrorResId(): LiveData<Int> {
        return errorResId
    }

    fun getCurrentUser(): UserModel {
        return userModel
    }
}