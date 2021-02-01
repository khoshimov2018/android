package ru.behetem.viewmodels

import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ru.behetem.R
import ru.behetem.models.UserModel

class HeightWeightViewModel: BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private val growthValue = MutableLiveData<Int>()
    private val weightValue = MutableLiveData<Int>()
    private lateinit var apiResponse: LiveData<UserModel>
    private lateinit var observeResponse: Observer<UserModel>

    private val growthErrorResId: MutableLiveData<Int> = MutableLiveData()
    private val weightErrorResId: MutableLiveData<Int> = MutableLiveData()

    override fun moveFurther(view: View) {
        userModelLiveData.value?.let{
            if(it.growth == null || it.growth == 0) {
                growthErrorResId.value = R.string.enter_growth
            } else if (it.weight == null || it.weight == 0) {
                weightErrorResId.value = R.string.enter_weight
            } else {
                moveFurther.value = true
            }
        }
    }

    fun onSkipClicked(view: View) {
        moveFurther.value = true
    }

    fun onGrowthChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        growthValue.value = progress
        userModelLiveData.value?.let {
            it.growth = progress
        }
        growthErrorResId.value = null
    }

    fun onWeightChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        weightValue.value = progress
        userModelLiveData.value?.let {
            it.weight = progress
        }
        weightErrorResId.value = null
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

    fun getGrowthErrorResId(): LiveData<Int> {
        return growthErrorResId
    }

    fun getWeightErrorResId(): LiveData<Int> {
        return weightErrorResId
    }
}