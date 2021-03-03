package ru.behetem.viewmodels

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ru.behetem.models.UserModel
import ru.behetem.repositories.UserRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.Constants
import ru.behetem.utils.hideKeyboard
import ru.behetem.utils.validateInternet
import ru.behetem.utils.validateResponse

class AddPhotosViewModel : BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private val openImagePicker: MutableLiveData<Boolean> = MutableLiveData()
    private val showLessImagesError: MutableLiveData<Boolean> = MutableLiveData()

    private var currentImageForPosition = -1
    private var numberOfImagesUploaded = 0
    private lateinit var view: View

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>

    fun choosePhotoClicked(view: View, position: Int) {
        this.view = view
        currentImageForPosition = position
        openImagePicker.value = true
    }

    fun setImageUri(uri: Uri) {
        uploadImage(uri)
    }

    private fun uploadImage(uri: Uri) {
        if(validateInternet(view.context)) {
            hideKeyboard(view)
            loaderVisible.value = true // show loader
            observeResponse = Observer<BaseResponse> {
                loaderVisible.value = false
                if(validateResponse(view.context, it)){
                    ++numberOfImagesUploaded
                }
            }

            // token
            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"

            val inputStream = view.context.contentResolver.openInputStream(uri)!!
            apiResponse = UserRepository.uploadImage(inputStream, strToken)
            apiResponse.observeForever(observeResponse)
        }
    }

    override fun moveFurther(view: View) {
        moveFurther.value = true
        /*if(numberOfImagesUploaded < Constants.MINIMUM_PHOTOS) {
            showLessImagesError.value = true
        } else {
            moveFurther.value = true
        }*/
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

    fun getOpenImagePicker(): LiveData<Boolean> {
        return openImagePicker
    }

    fun setOpenImagePicker(open: Boolean) {
        openImagePicker.value = open
    }

    fun getCurrentImageForPosition(): Int {
        return currentImageForPosition
    }

    fun getShowLessImagesError(): LiveData<Boolean> {
        return showLessImagesError
    }

    fun setShowLessImagesError(show: Boolean) {
        showLessImagesError.value = show
    }
}