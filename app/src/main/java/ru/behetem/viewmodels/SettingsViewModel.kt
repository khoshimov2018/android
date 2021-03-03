package ru.behetem.viewmodels

import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ru.behetem.R
import ru.behetem.repositories.UserRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.*

class SettingsViewModel : BaseViewModel() {

    private val versionName: MutableLiveData<String> = MutableLiveData()
    private val aboutUsClicked: MutableLiveData<Boolean> = MutableLiveData()
    private val helpClicked: MutableLiveData<Boolean> = MutableLiveData()
    private val feedbackClicked: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToChangePassword: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>

    fun onChangePasswordClicked(view: View) {
        moveToChangePassword.value = true
    }

    fun onAboutUsClicked(view: View) {
        aboutUsClicked.value = true
    }

    fun onHelpClicked(view: View) {
        helpClicked.value = true
    }

    fun onFeedbackClicked(view: View) {
        feedbackClicked.value = true
    }

    fun onLogoutClicked(view: View) {
        val context = view.context
        showAlertDialog(
            context,
            null,
            context.getString(R.string.sure_logout),
            context.getString(R.string.yes),
            DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.cancel()
                logoutAndMoveToHome(context)
            },
            context.getString(R.string.no),
            null
        )
    }

    fun onDeleteAccountClicked(view: View) {
        val context = view.context
        showAlertDialog(
            context,
            null,
            context.getString(R.string.sure_delete_account),
            context.getString(R.string.yes),
            DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.cancel()
                deleteAccount(view)
            },
            context.getString(R.string.no),
            null
        )
    }

    private fun deleteAccount(view: View) {
        if(validateInternet(view.context)) {
            loaderVisible.value = true // show loader
            observeResponse = Observer<BaseResponse> { response ->
                loaderVisible.value = false
                if (validateResponse(view.context, response)) {
                    logoutAndMoveToHome(view.context)
                }
            }
            // token
            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"

            apiResponse = UserRepository.deleteAccount(strToken)
            apiResponse.observeForever(observeResponse)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (this::apiResponse.isInitialized) {
            apiResponse.removeObserver(observeResponse)
        }
    }

    fun getVersionName(): LiveData<String> {
        return versionName
    }

    fun setVersionName(versionName: String) {
        this.versionName.value = versionName
    }

    fun getAboutUsClicked(): LiveData<Boolean> {
        return aboutUsClicked
    }

    fun setAboutUsClicked(clicked: Boolean) {
        aboutUsClicked.value = clicked
    }

    fun getHelpClicked(): LiveData<Boolean> {
        return helpClicked
    }

    fun setHelpClicked(clicked: Boolean) {
        helpClicked.value = clicked
    }

    fun getFeedbackClicked(): LiveData<Boolean> {
        return feedbackClicked
    }

    fun setFeedbackClicked(clicked: Boolean) {
        feedbackClicked.value = clicked
    }

    fun getMoveToChangePassword(): LiveData<Boolean> {
        return moveToChangePassword
    }

    fun setMoveToChangePassword(move: Boolean) {
        moveToChangePassword.value = move
    }
}