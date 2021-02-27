package ru.behetem.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.behetem.R
import ru.behetem.interfaces.IInterestClick
import ru.behetem.models.InterestModel
import ru.behetem.models.UserModel
import ru.behetem.repositories.InterestsRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.isInternetAvailable
import ru.behetem.utils.validateResponseWithoutPopup

class MyInterestsViewModel(application: Application) : BaseAndroidViewModel(application),
    IInterestClick {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>
    private val interestsList: MutableLiveData<MutableList<InterestModel>> = MutableLiveData()
    private val errorResId: MutableLiveData<Int> = MutableLiveData()
    private val baseResponse: MutableLiveData<BaseResponse> = MutableLiveData()

    fun getInterests() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            observeResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
                    if (it.data is MutableList<*>) {
                        val gson = Gson()
                        val strResponse = gson.toJson(it.data)
                        val myType = object : TypeToken<MutableList<InterestModel>>() {}.type
                        val interests: MutableList<InterestModel> =
                            gson.fromJson<MutableList<InterestModel>>(strResponse, myType)

                        for (interest in interests) {
                            interest.isSelected = false
                        }

                        interestsList.value = interests
                    }
                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            apiResponse = InterestsRepository.getInterests(strToken)
            apiResponse.observeForever(observeResponse)
        } else {
            showNoInternet.value = true
        }
    }

    override fun interestItemClicked(view: View, interestItem: InterestModel) {
        interestItem.isSelected = interestItem.isSelected == null || !interestItem.isSelected!!
        interestsList.value = interestsList.value
        errorResId.value = null
    }

    override fun moveFurther(view: View) {
        if (userModelLiveData.value?.interests == null) {
            userModelLiveData.value?.interests = ArrayList<String>()
        } else {
            userModelLiveData.value?.interests?.clear()
        }

        interestsList.value?.let {
            for (interest in it) {
                if (interest.isSelected != null && interest.isSelected!!) {
                    userModelLiveData.value?.interests?.add(interest.interestId!!)
                }
            }
        }

        if (userModelLiveData.value?.interests != null && userModelLiveData.value?.interests?.size!! > 0) {
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

    fun getBaseResponse(): LiveData<BaseResponse?> {
        return baseResponse
    }

    fun setBaseResponse(baseResponse: BaseResponse?) {
        this.baseResponse.value = baseResponse
    }
}