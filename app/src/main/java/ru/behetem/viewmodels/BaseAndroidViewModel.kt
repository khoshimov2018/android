package ru.behetem.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.behetem.models.UserModel
import ru.behetem.responses.BaseResponse

open class BaseAndroidViewModel(application: Application): AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    val context = application.applicationContext
    private lateinit var loggedInUser: UserModel
    val showNoInternet: MutableLiveData<Boolean> = MutableLiveData()
    val moveFurther: MutableLiveData<Boolean> = MutableLiveData()
    val baseResponse: MutableLiveData<BaseResponse> = MutableLiveData()

    private val backButtonClicked: MutableLiveData<Boolean> = MutableLiveData()
    val loaderVisible: MutableLiveData<Boolean> = MutableLiveData()

    fun backPressed(view: View) {
        backButtonClicked.value = true
    }

    fun getBackButtonClicked(): LiveData<Boolean> {
        return backButtonClicked
    }

    fun getLoaderVisible(): LiveData<Boolean> {
        return loaderVisible
    }

    fun setLoaderVisible(visible: Boolean) {
        loaderVisible.value = visible
    }

    open fun setLoggedInUser(user: UserModel) {
        loggedInUser = user
    }

    fun getLoggedInUser(): UserModel? {
        return if (this::loggedInUser.isInitialized)
            loggedInUser
        else null
    }

    fun getShowNoInternet(): LiveData<Boolean> {
        return showNoInternet
    }

    fun setShowNoInternet(showNoInternet: Boolean) {
        this.showNoInternet.value = showNoInternet
    }

    open fun moveFurther(view: View) {
        moveFurther.value = true
    }

    fun getMoveFurther(): LiveData<Boolean> {
        return moveFurther
    }

    fun setMoveFurther(move: Boolean) {
        moveFurther.value = move
    }

    fun getBaseResponse(): LiveData<BaseResponse?> {
        return baseResponse
    }

    fun setBaseResponse(baseResponse: BaseResponse?) {
        this.baseResponse.value = baseResponse
    }
}