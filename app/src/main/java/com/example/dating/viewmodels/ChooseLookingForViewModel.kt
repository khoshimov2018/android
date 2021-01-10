package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dating.R
import com.example.dating.models.UserModel
import com.example.dating.utils.Constants
import com.example.dating.utils.Gender
import com.example.dating.utils.SharedPreferenceHelper
import com.example.dating.utils.saveLoggedInUserToShared

class ChooseLookingForViewModel : BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private val loggedInUserLiveData = MutableLiveData<UserModel>()
    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    override fun moveFurther(view: View) {
        loggedInUserLiveData.value?.let {
            if(it.isLookingForSelected()) {
                SharedPreferenceHelper.saveBooleanToShared(
                    view.context,
                    Constants.IS_USER_LOGGED_IN,
                    true
                )
                moveFurther.value = true
            } else {
                errorResId.value = R.string.choose_looking_for
            }
        }
    }

    fun onMaleClicked(view: View) {
        loggedInUserLiveData.value?.lookingFor = Gender.MALE
        loggedInUserLiveData.value = loggedInUserLiveData.value
        saveLoggedInUserToShared(view.context, loggedInUserLiveData.value!!)
        errorResId.value = null
    }

    fun onFemaleClicked(view: View) {
        loggedInUserLiveData.value?.lookingFor = Gender.FEMALE
        loggedInUserLiveData.value = loggedInUserLiveData.value
        saveLoggedInUserToShared(view.context, loggedInUserLiveData.value!!)
        errorResId.value = null
    }

    override fun setLoggedInUser(user: UserModel) {
        super.setLoggedInUser(user)
        loggedInUserLiveData.value = user
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

    fun getLoggedInUserLiveData(): LiveData<UserModel> {
        return loggedInUserLiveData
    }

    fun getErrorResId(): LiveData<Int> {
        return errorResId
    }
}