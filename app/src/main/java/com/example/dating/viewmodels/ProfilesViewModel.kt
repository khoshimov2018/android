package com.example.dating.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.dating.models.FilterModel
import com.example.dating.models.NationalityModel
import com.example.dating.models.UserModel
import com.example.dating.repositories.FiltersRepository
import com.example.dating.repositories.NationalitiesRepository
import com.example.dating.repositories.UserRepository
import com.example.dating.responses.BaseResponse
import com.example.dating.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProfilesViewModel(application: Application) : BaseAndroidViewModel(application) {

    private var filterModelLiveData: MutableLiveData<FilterModel> = MutableLiveData()
    private val baseResponse: MutableLiveData<BaseResponse> = MutableLiveData()

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>

    private lateinit var usersApiResponse: LiveData<BaseResponse>
    private lateinit var usersObserveResponse: Observer<BaseResponse>

    fun getFilters() {
        filterModelLiveData.value = getFiltersFromShared(context)
        if (filterModelLiveData.value == null) {
            fetchFilters()
        }
    }

    private fun fetchFilters() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            observeResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
                    val gson = Gson()
                    val strResponse = gson.toJson(it.data)
                    val myType = object : TypeToken<FilterModel>() {}.type
                    val responseFilter: FilterModel =
                        gson.fromJson<FilterModel>(strResponse, myType)

                    filterModelLiveData.value = responseFilter
                    filterModelLiveData.value?.let { filter ->
                        saveFiltersToShared(context, filter)
                    }
                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            apiResponse = FiltersRepository.getFilters(strToken)
            apiResponse.observeForever(observeResponse)
        } else {
            showNoInternet.value = true
        }
    }

    fun getUsers() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            usersObserveResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {

                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            filterModelLiveData.value?.let {
                usersApiResponse = UserRepository.getUsers(strToken, it)
            }
            usersApiResponse.observeForever(usersObserveResponse)
        } else {
            showNoInternet.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (this::apiResponse.isInitialized) {
            apiResponse.removeObserver(observeResponse)
        }
        if (this::usersApiResponse.isInitialized) {
            usersApiResponse.removeObserver(usersObserveResponse)
        }
    }

    fun getBaseResponse(): LiveData<BaseResponse?> {
        return baseResponse
    }

    fun setBaseResponse(baseResponse: BaseResponse?) {
        this.baseResponse.value = baseResponse
    }

    fun getFilterModelLiveData(): LiveData<FilterModel> {
        return filterModelLiveData
    }
}