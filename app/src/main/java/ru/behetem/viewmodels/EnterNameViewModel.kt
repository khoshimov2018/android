package ru.behetem.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.behetem.R
import ru.behetem.models.UserModel

class EnterNameViewModel : BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    override fun moveFurther(view: View) {
        userModelLiveData.value?.let {
            if(it.isNameEmpty()) {
                errorResId.value = R.string.enter_name
            } else {
                moveFurther.value = true
            }
        }
    }

    fun onNameTextChanged(charSequence: CharSequence) {
        userModelLiveData.value?.name = charSequence.toString()
        errorResId.value = null
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

    fun getErrorResId(): LiveData<Int> {
        return errorResId
    }
}