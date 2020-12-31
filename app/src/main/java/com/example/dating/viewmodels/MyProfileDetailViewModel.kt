package com.example.dating.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MyProfileDetailViewModel: BaseViewModel() {

    private val numberOfImages: MutableLiveData<Int> = MutableLiveData()

    fun getNumberOfImages(): LiveData<Int> {
        return numberOfImages
    }
}