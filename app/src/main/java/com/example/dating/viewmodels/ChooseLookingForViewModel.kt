package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.dating.R
import com.example.dating.models.FilterModel
import com.example.dating.models.UserModel
import com.example.dating.repositories.FiltersRepository
import com.example.dating.repositories.UserRepository
import com.example.dating.responses.BaseResponse
import com.example.dating.utils.*

class ChooseLookingForViewModel : BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private val loggedInUserLiveData = MutableLiveData<UserModel>()
    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>

    private val filterModelLiveData: MutableLiveData<FilterModel> = MutableLiveData(FilterModel())

    override fun moveFurther(view: View) {
        filterModelLiveData.value?.let {
            if(it.isLookingForSelected()) {
                saveFilters(view)
            } else {
                errorResId.value = R.string.choose_looking_for
            }
        }
    }

    private fun saveFilters(view: View) {
        if(validateInternet(view.context)) {
            loaderVisible.value = true // show loader
            observeResponse = Observer<BaseResponse> { response ->
                loaderVisible.value = false
                if (validateResponse(view.context, response)) {
                    SharedPreferenceHelper.saveBooleanToShared(
                        view.context,
                        Constants.IS_USER_LOGGED_IN,
                        true
                    )
                    moveFurther.value = true
                }
            }
            // token
            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"

            filterModelLiveData.value?.let {
                apiResponse = FiltersRepository.saveFilters(it, strToken)
            }
            apiResponse.observeForever(observeResponse)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (this::apiResponse.isInitialized) {
            apiResponse.removeObserver(observeResponse)
        }
    }

    fun onMaleClicked(view: View) {
        filterModelLiveData.value?.gender = Gender.MALE
        filterModelLiveData.value = filterModelLiveData.value
        filterModelLiveData.value?.let {
            saveFiltersToShared(view.context, it)
        }
        errorResId.value = null
    }

    fun onFemaleClicked(view: View) {
        filterModelLiveData.value?.gender = Gender.FEMALE
        filterModelLiveData.value = filterModelLiveData.value
        filterModelLiveData.value?.let {
            saveFiltersToShared(view.context, it)
        }
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

    fun getFilterModelLiveData(): LiveData<FilterModel> {
        return filterModelLiveData
    }
}