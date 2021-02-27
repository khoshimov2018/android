package ru.behetem.viewmodels

import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ru.behetem.R
import ru.behetem.models.BodyInfoModel
import ru.behetem.models.UserModel
import ru.behetem.utils.BodyType

class HeightWeightViewModel: BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private val growthValue = MutableLiveData<Int>()
    private val weightValue = MutableLiveData<Int>()
    private lateinit var apiResponse: LiveData<UserModel>
    private lateinit var observeResponse: Observer<UserModel>

    private val growthErrorResId: MutableLiveData<Int> = MutableLiveData()
    private val weightErrorResId: MutableLiveData<Int> = MutableLiveData()
    private val bodyTypeErrorResId: MutableLiveData<Int> = MutableLiveData()

    override fun moveFurther(view: View) {
        if(userModelLiveData.value?.bodyInfo == null) {
            userModelLiveData.value?.bodyInfo = BodyInfoModel()
        }

        userModelLiveData.value?.let{
            if(it.bodyInfo?.growth == null || it.bodyInfo?.growth == 0) {
                growthErrorResId.value = R.string.enter_growth
            } else if (it.bodyInfo?.weight == null || it.bodyInfo?.weight == 0) {
                weightErrorResId.value = R.string.enter_weight
            } else if (userModelLiveData.value?.bodyInfo?.bodyType == null) {
                bodyTypeErrorResId.value = R.string.choose_body_type
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
            it.bodyInfo?.growth = progress
        }
        growthErrorResId.value = null
    }

    fun onWeightChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        weightValue.value = progress
        userModelLiveData.value?.let {
            it.bodyInfo?.weight = progress
        }
        weightErrorResId.value = null
    }

    fun onSkinnyClicked(view: View) {
        if(userModelLiveData.value?.bodyInfo == null) {
            userModelLiveData.value?.bodyInfo = BodyInfoModel()
        }
        userModelLiveData.value?.bodyInfo?.bodyType = BodyType.THIN
        bodyTypeErrorResId.value = null
    }

    fun onSlenderClicked(view: View) {
        if(userModelLiveData.value?.bodyInfo == null) {
            userModelLiveData.value?.bodyInfo = BodyInfoModel()
        }
        userModelLiveData.value?.bodyInfo?.bodyType = BodyType.SLIM
        bodyTypeErrorResId.value = null
    }

    fun onSportsClicked(view: View) {
        if(userModelLiveData.value?.bodyInfo == null) {
            userModelLiveData.value?.bodyInfo = BodyInfoModel()
        }
        userModelLiveData.value?.bodyInfo?.bodyType = BodyType.ATHLETIC
        bodyTypeErrorResId.value = null
    }

    fun onDenseClicked(view: View) {
        if(userModelLiveData.value?.bodyInfo == null) {
            userModelLiveData.value?.bodyInfo = BodyInfoModel()
        }
        userModelLiveData.value?.bodyInfo?.bodyType = BodyType.PLUMP
        bodyTypeErrorResId.value = null
    }

    fun onCompleteClicked(view: View) {
        if(userModelLiveData.value?.bodyInfo == null) {
            userModelLiveData.value?.bodyInfo = BodyInfoModel()
        }
        userModelLiveData.value?.bodyInfo?.bodyType = BodyType.FAT
        bodyTypeErrorResId.value = null
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

    fun getBodyTypeErrorResId(): LiveData<Int> {
        return bodyTypeErrorResId
    }
}