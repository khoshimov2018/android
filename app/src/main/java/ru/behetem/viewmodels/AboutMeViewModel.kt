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

class AboutMeViewModel : BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>
    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    override fun moveFurther(view: View) {
        userModelLiveData.value?.let {
            if(it.description.isNullOrEmpty()) {
                errorResId.value = R.string.enter_about_me
            } else {
                submitData(view)
            }
        }
    }

    fun onSkipClicked(view: View) {
        submitData(view)
    }

    private fun submitData(view: View) {
        if(validateInternet(view.context)) {
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

    fun onAboutMeTextChanged(charSequence: CharSequence) {
        userModelLiveData.value?.let {
            it.description = charSequence.toString()
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