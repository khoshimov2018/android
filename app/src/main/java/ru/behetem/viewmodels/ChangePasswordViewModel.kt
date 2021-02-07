package ru.behetem.viewmodels

import android.content.DialogInterface
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.behetem.R
import ru.behetem.models.ChangePasswordModel
import ru.behetem.models.PasswordModel
import ru.behetem.models.UserModel
import ru.behetem.repositories.UserRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.*

class ChangePasswordViewModel: BaseViewModel() {

    private val passwordModel = PasswordModel()
    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>

    fun updatePasswordClicked(view: View) {
        when (passwordModel.validateChangePassword()) {
            ChangePasswordFormErrorConstants.OLD_EMPTY -> {
                errorResId.value = R.string.enter_old_password
            }
            ChangePasswordFormErrorConstants.NEW_EMPTY -> {
                errorResId.value = R.string.enter_new_password
            }
            ChangePasswordFormErrorConstants.CONFIRM_EMPTY -> {
                errorResId.value = R.string.enter_confirm_password
            }
            ChangePasswordFormErrorConstants.DO_NOT_MATCH -> {
                errorResId.value = R.string.passwords_do_not_match
            }
            else -> {
                if (validateInternet(view.context)) {
                    hideKeyboard(view)
                    loaderVisible.value = true // show loader
                    observeResponse = Observer<BaseResponse> {
                        loaderVisible.value = false
                        if (validateResponse(view.context, it)) {
                            showAlertDialog(view.context,
                                null,
                                view.context.getString(R.string.passwords_changed_successfully),
                                view.context.getString(R.string.ok),
                                DialogInterface.OnClickListener { dialogInterface, _ ->
                                    dialogInterface.cancel()
                                    backPressed(view)
                                },
                                null,
                                null
                            )
                        }
                    }
                    val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
                    val changePasswordModel = ChangePasswordModel(passwordModel)
                    apiResponse = UserRepository.changePassword(strToken, changePasswordModel)
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

    fun onOldPasswordTextChanged(charSequence: CharSequence) {
        passwordModel.old = charSequence.toString()
        errorResId.value = null
    }

    fun onNewPasswordTextChanged(charSequence: CharSequence) {
        passwordModel.new = charSequence.toString()
        errorResId.value = null
    }

    fun onConfirmPasswordTextChanged(charSequence: CharSequence) {
        passwordModel.confirm = charSequence.toString()
        errorResId.value = null
    }

    fun getErrorResId(): LiveData<Int> {
        return errorResId
    }
}