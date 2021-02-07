package ru.behetem.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.behetem.R
import ru.behetem.interfaces.INationalityClick
import ru.behetem.models.NationalityModel
import ru.behetem.models.UserModel
import ru.behetem.repositories.NationalitiesRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.isInternetAvailable
import ru.behetem.utils.validateResponseWithoutPopup

class NationalitiesViewModel(application: Application): BaseAndroidViewModel(application),
    INationalityClick {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>
    private val nationalitiesList: MutableLiveData<MutableList<NationalityModel>> = MutableLiveData()
    private val errorResId: MutableLiveData<Int> = MutableLiveData()
    private val baseResponse: MutableLiveData<BaseResponse> = MutableLiveData()

    fun getNationalities() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            observeResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
                    if (it.data is MutableList<*>) {
                        val gson = Gson()
                        val strResponse = gson.toJson(it.data)
                        val myType = object : TypeToken<MutableList<NationalityModel>>() {}.type
                        val nationalities: MutableList<NationalityModel> = gson.fromJson<MutableList<NationalityModel>>(strResponse, myType)

                        for(nationality in nationalities) {
                            nationality.isSelected = false
                        }

                        nationalitiesList.value = nationalities
                    }
                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            apiResponse = NationalitiesRepository.getNationalities(strToken)
            apiResponse.observeForever(observeResponse)
        } else {
            showNoInternet.value = true
        }
    }

    override fun nationalityItemClicked(view: View, nationalityItem: NationalityModel) {
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
                    userModelLiveData.value?.nationality = nationality.getLabelToShow(getChosenGender())
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

    fun getChosenGender(): String {
        return if(this.userModelLiveData.value != null && this.userModelLiveData.value!!.gender != null) {
            this.userModelLiveData.value!!.gender!!
        } else {
            ""
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

    fun getBaseResponse(): LiveData<BaseResponse?> {
        return baseResponse
    }

    fun setBaseResponse(baseResponse: BaseResponse?) {
        this.baseResponse.value = baseResponse
    }
}