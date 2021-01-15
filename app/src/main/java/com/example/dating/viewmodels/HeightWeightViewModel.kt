package com.example.dating.viewmodels

import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.dating.models.UserModel

class HeightWeightViewModel: BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private val growthValue = MutableLiveData<Int>()
    private val weightValue = MutableLiveData<Int>()
    private lateinit var apiResponse: LiveData<UserModel>
    private lateinit var observeResponse: Observer<UserModel>

    fun onGrowthChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        growthValue.value = progress
        userModelLiveData.value?.let {
            it.growth = progress
        }
    }

    fun onWeightChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        weightValue.value = progress
        userModelLiveData.value?.let {
            it.weight = progress
        }
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

    fun getGrowthValue(): LiveData<Int> {
        return growthValue
    }

    fun getWeightValue(): LiveData<Int> {
        return weightValue
    }
}