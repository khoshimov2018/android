package com.example.dating.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.dating.R
import com.example.dating.models.InterestModel
import com.example.dating.models.NationalityModel
import com.example.dating.models.UserModel
import com.example.dating.repositories.InterestsRepository
import com.example.dating.repositories.NationalitiesRepository
import com.example.dating.utils.isInternetAvailable
import com.example.dating.utils.validateResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NationalitiesViewModel(application: Application): BaseAndroidViewModel(application) {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private lateinit var apiResponse: LiveData<Any>
    private lateinit var observeResponse: Observer<Any>
    private val nationalitiesList: MutableLiveData<MutableList<NationalityModel>> = MutableLiveData()
    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    fun getNationalities() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            observeResponse = Observer<Any> {
                loaderVisible.value = false

                if (it is MutableList<*>) {
                    val gson = Gson()
                    val strResponse = gson.toJson(it)
                    val myType = object : TypeToken<MutableList<String>>() {}.type
                    val nationalitiesStringList: MutableList<String> = gson.fromJson<MutableList<String>>(strResponse, myType)

                    val tempNationalitiesList = ArrayList<NationalityModel>()

                    for(nationality in nationalitiesStringList) {
                        tempNationalitiesList.add(NationalityModel(nationality, false))
                    }

                    nationalitiesList.value = tempNationalitiesList
                } else if (it is UserModel) {
                    validateResponse(context, it)
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            apiResponse = NationalitiesRepository.getNationalities(strToken)
            apiResponse.observeForever(observeResponse)
        } else {
            showNoInternet.value = true
        }
    }

    fun nationalityItemClicked(view: View, nationalityItem: NationalityModel) {
        nationalitiesList.value?.let {
            for(nationality in it) {
                nationality.isSelected = false
            }
        }
        nationalityItem.isSelected = true
        nationalitiesList.value = nationalitiesList.value
        errorResId.value = null
    }

    override fun moveFurther(view: View) {
        nationalitiesList.value?.let {
            for(nationality in it) {
                if(nationality.isSelected != null && nationality.isSelected!!) {
                    userModelLiveData.value?.nationality = nationality.label
                    break
                }
            }
        }
        if(userModelLiveData.value?.nationality == null) {
            errorResId.value = R.string.choose_nationality
        } else {
            moveFurther.value = true
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

    fun getNationalitiesList(): LiveData<MutableList<NationalityModel>> {
        return nationalitiesList
    }

    fun getErrorResId(): LiveData<Int> {
        return errorResId
    }
}