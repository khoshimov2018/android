package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.dating.R
import com.example.dating.models.UserModel
import com.example.dating.repositories.UserRepository
import com.example.dating.responses.BaseResponse
import com.example.dating.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LoginViewModel : BaseViewModel() {

    private val user: UserModel = UserModel()

    private val errorResId: MutableLiveData<Int> = MutableLiveData()
    private val moveToHome: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToForgotPassword: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>

    fun loginClicked(view: View) {
        when (user.validateLoginData()) {
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
                if (validateInternet(view.context)) {
                    hideKeyboard(view)
                    loaderVisible.value = true // show loader
                    observeResponse = Observer<BaseResponse> {
                        loaderVisible.value = false
                        if (validateResponse(view.context, it)) {
                            val gson = Gson()
                            val strResponse = gson.toJson(it.data)
                            val myType = object : TypeToken<UserModel>() {}.type
                            val responseUser: UserModel =
                                gson.fromJson<UserModel>(strResponse, myType)

                            // save to shared
                            SharedPreferenceHelper.saveBooleanToShared(
                                view.context,
                                Constants.IS_USER_LOGGED_IN,
                                true
                            )
                            saveLoggedInUserToShared(view.context, responseUser)
                            moveToHome.value = true
                        }
                    }
                    apiResponse = UserRepository.login(user)
                    apiResponse.observeForever(observeResponse)
                }
            }
        }
    }

    fun forgotPasswordClicked(view: View) {
        moveToForgotPassword.value = true
    }

    override fun onCleared() {
        super.onCleared()
        if (this::apiResponse.isInitialized) {
            apiResponse.removeObserver(observeResponse)
        }
    }

    fun onUsernameTextChanged(charSequence: CharSequence) {
        user.username = charSequence.toString()
        errorResId.value = null
    }

    fun onPasswordTextChanged(charSequence: CharSequence) {
        user.password = charSequence.toString()
        errorResId.value = null
    }

    fun getErrorResId(): LiveData<Int> {
        return errorResId
    }

    fun getMoveToHome(): LiveData<Boolean> {
        return moveToHome
    }

    fun setMoveToHome(move: Boolean) {
        moveToHome.value = move
    }

    fun getMoveToForgotPassword(): LiveData<Boolean> {
        return moveToForgotPassword
    }

    fun setMoveToForgotPassword(move: Boolean) {
        moveToForgotPassword.value = move
    }
}