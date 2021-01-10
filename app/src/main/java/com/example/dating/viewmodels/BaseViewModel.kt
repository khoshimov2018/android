package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dating.models.UserModel

open class BaseViewModel : ViewModel() {

    private val backButtonClicked: MutableLiveData<Boolean> = MutableLiveData()
    val loaderVisible: MutableLiveData<Boolean> = MutableLiveData()
    val moveFurther: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var loggedInUser: UserModel

    fun backPressed(view: View) {
        backButtonClicked.value = true
    }

    open fun moveFurther(view: View) {
        moveFurther.value = true
    }

    fun getBackButtonClicked(): LiveData<Boolean> {
        return backButtonClicked
    }

    fun getMoveFurther(): LiveData<Boolean> {
        return moveFurther
    }

    fun setMoveFurther(move: Boolean) {
        moveFurther.value = move
    }

    fun getLoaderVisible(): LiveData<Boolean> {
        return loaderVisible
    }

    open fun setLoggedInUser(user: UserModel) {
        loggedInUser = user
    }

    fun getLoggedInUser(): UserModel? {
        return if (this::loggedInUser.isInitialized)
            loggedInUser
        else null
    }
}