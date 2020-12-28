package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChooseGenderViewModel : ViewModel() {

    private val backButtonClicked: MutableLiveData<Boolean> = MutableLiveData()

    fun backPressed(view: View) {
        backButtonClicked.value = true
    }

    fun getBackButtonClicked(): LiveData<Boolean> {
        return backButtonClicked
    }
}