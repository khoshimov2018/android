package com.example.dating.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.dating.R
import com.example.dating.models.InterestModel
import com.example.dating.models.UserModel
import com.example.dating.repositories.InterestsRepository
import com.example.dating.repositories.UserRepository
import com.example.dating.utils.isInternetAvailable
import com.example.dating.utils.printLog
import com.example.dating.utils.showInfoAlertDialog
import com.example.dating.utils.validateResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyInterestsViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private lateinit var apiResponse: LiveData<Any>
    private lateinit var observeResponse: Observer<Any>
    private val interestsList: MutableLiveData<MutableList<InterestModel>> = MutableLiveData()
    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    fun getInterests() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            observeResponse = Observer<Any> {
                loaderVisible.value = false

                if (it is MutableList<*>) {
                    val gson = Gson()
                    val strResponse = gson.toJson(it)
                    val myType = object : TypeToken<MutableList<String>>() {}.type
                    val interestStringList: MutableList<String> = gson.fromJson<MutableList<String>>(strResponse, myType)

                    val tempInterestsList = ArrayList<InterestModel>()

                    for(interest in interestStringList) {
                        tempInterestsList.add(InterestModel(interest, false))
                    }

                    interestsList.value = tempInterestsList
                } else if (it is UserModel) {
                    validateResponse(context, it)
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            apiResponse = InterestsRepository.getInterests(strToken)
            apiResponse.observeForever(observeResponse)
        } else {
            showNoInternet.value = true
        }
    }

    fun interestItemClicked(view: View, interestItem: InterestModel) {
        interestItem.isSelected = interestItem.isSelected == null || !interestItem.isSelected!!
        interestsList.value = interestsList.value
        errorResId.value = null
    }

    override fun moveFurther(view: View) {
        if(userModelLiveData.value?.interestLabels == null) {
            userModelLiveData.value?.interestLabels = ArrayList<String>()
        } else {
            userModelLiveData.value?.interestLabels?.clear()
        }

        interestsList.value?.let {
            for(interest in it) {
                if(interest.isSelected != null && interest.isSelected!!) {
                    userModelLiveData.value?.interestLabels?.add(interest.label!!)
                }
            }
        }

        if(userModelLiveData.value?.interestLabels != null && userModelLiveData.value?.interestLabels?.size!! > 0) {
            moveFurther.value = true
        } else {
            errorResId.value = R.string.choose_interests
        }
    }

    fun onSkipClicked(view: View) {
        moveFurther.value = true
    }

    override fun onCleared() {
        super.onCleared()
        if (this::apiResponse.isInitialized) {
            apiResponse.removeObserver(observeResponse)
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

    fun getInterestsList(): LiveData<MutableList<InterestModel>> {
        return interestsList
    }

    fun getErrorResId(): LiveData<Int> {
        return errorResId
    }
}