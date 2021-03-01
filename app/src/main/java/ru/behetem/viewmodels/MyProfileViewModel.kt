package ru.behetem.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.behetem.models.*
import ru.behetem.repositories.UserRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.ApiConstants
import ru.behetem.utils.isInternetAvailable
import ru.behetem.utils.validateResponseWithoutPopup

class MyProfileViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val moveToSettings: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToProfile: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToCoins: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToPremium: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>

    private val userProfileLiveData: MutableLiveData<UserModel> = MutableLiveData()
    private val imagesListLiveData: MutableLiveData<MutableList<String>> = MutableLiveData()

    fun getUserProfile() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            observeResponse = Observer<BaseResponse> {
                loaderVisible.value = false
                if (validateResponseWithoutPopup(it)) {
                    val gson = Gson()
                    val strResponse = gson.toJson(it.data)
                    val myType = object : TypeToken<UserModel>() {}.type
                    val responseUser: UserModel = gson.fromJson<UserModel>(strResponse, myType)

                    userProfileLiveData.value = responseUser
                } else {
                    baseResponse.value = it
                }

                getCurrentUserImages()
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            apiResponse = UserRepository.getInfo(strToken)
            apiResponse.observeForever(observeResponse)
        } else {
            showNoInternet.value = true
        }
    }

    private fun getCurrentUserImages() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            observeResponse = Observer<BaseResponse> {
                loaderVisible.value = false
                if (validateResponseWithoutPopup(it)) {
                    if (it.data is MutableList<*>) {
                        val gson = Gson()
                        val strResponse = gson.toJson(it.data)
                        val myType = object : TypeToken<MutableList<String>>() {}.type
                        val receivedImagesList: MutableList<String> =
                            gson.fromJson<MutableList<String>>(strResponse, myType)

                        val imagesList = ArrayList<String>()
                        for(image in receivedImagesList) {
                            val imageUrl = "${ApiConstants.BASE_URL}$image"
                            imagesList.add(imageUrl)
                        }
                        imagesListLiveData.value = imagesList
                    }
                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            apiResponse = UserRepository.getCurrentUserImages(strToken)
            apiResponse.observeForever(observeResponse)
        } else {
            showNoInternet.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (this::apiResponse.isInitialized) {
            apiResponse.removeObserver(observeResponse)
        }
    }

    fun settingsClicked(view: View) {
        moveToSettings.value = true
    }

    fun profileClicked(view: View) {
        moveToProfile.value = true
    }

    fun coinsClicked(view: View) {
        moveToCoins.value = true
    }

    fun premiumClicked(view: View) {
        moveToPremium.value = true
    }

    fun getMoveToSettings(): LiveData<Boolean> {
        return moveToSettings
    }

    fun setMoveToSettings(move: Boolean) {
        moveToSettings.value = move
    }

    fun getMoveToProfile(): LiveData<Boolean> {
        return moveToProfile
    }

    fun setMoveToProfile(move: Boolean) {
        moveToProfile.value = move
    }

    fun getMoveToCoins(): LiveData<Boolean> {
        return moveToCoins
    }

    fun setMoveToCoins(move: Boolean) {
        moveToCoins.value = move
    }

    fun getMoveToPremium(): LiveData<Boolean> {
        return moveToPremium
    }

    fun setMoveToPremium(move: Boolean) {
        moveToPremium.value = move
    }

    fun getUserProfileLiveData(): LiveData<UserModel> {
        return userProfileLiveData
    }

    fun getCurrentUser(): UserModel? {
        return userProfileLiveData.value
    }

    fun getImagesListLiveData(): LiveData<MutableList<String>> {
        return imagesListLiveData
    }

    fun getImages(): MutableList<String>? {
        return imagesListLiveData.value
    }

    fun setCurrentUser(userModel: UserModel) {
        userProfileLiveData.value = userModel
    }
}