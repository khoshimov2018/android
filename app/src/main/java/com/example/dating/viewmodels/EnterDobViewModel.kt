package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EnterDobViewModel : ViewModel() {

    private val backButtonClicked: MutableLiveData<Boolean> = MutableLiveData()
    private val moveFurther: MutableLiveData<Boolean> = MutableLiveData()

    fun backPressed(view: View) {
        backButtonClicked.value = true
    }

    fun moveFurther(view: View) {
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
}