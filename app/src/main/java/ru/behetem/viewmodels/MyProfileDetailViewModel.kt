package ru.behetem.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.behetem.interfaces.IInterestClick
import ru.behetem.models.InterestModel
import ru.behetem.models.UserModel

class MyProfileDetailViewModel: BaseViewModel(), IInterestClick {

    private val userProfileLiveData: MutableLiveData<UserModel> = MutableLiveData()
    private val moveToEditProfile: MutableLiveData<Boolean> = MutableLiveData()
    private val imagesListLiveData: MutableLiveData<MutableList<String>> = MutableLiveData()

    fun getInterestsList(): MutableList<InterestModel> {
        val list = ArrayList<InterestModel>()
        if(userProfileLiveData.value != null && userProfileLiveData.value?.interests != null) {
            for(interestLabel in userProfileLiveData.value!!.interests!!) {
                val interest = InterestModel()
                interest.label = interestLabel
                interest.isSelected = true
                list.add(interest)
            }
        }
        return list
    }

    override fun interestItemClicked(view: View, interestItem: InterestModel) {
        // Do nothing
    }

    fun editProfileClicked(view: View) {
        moveToEditProfile.value = true
    }

    fun getMoveToEditProfile(): LiveData<Boolean> {
        return moveToEditProfile
    }

    fun setMoveToEditProfile(move: Boolean) {
        moveToEditProfile.value = move
    }

    fun getUserProfileLiveData(): LiveData<UserModel> {
        return userProfileLiveData
    }

    fun setCurrentUser(userModel: UserModel) {
        userProfileLiveData.value = userModel
    }

    fun getCurrentUser(): UserModel? {
        return userProfileLiveData.value
    }

    fun setImagesListLiveData(imagesList: MutableList<String>) {
        imagesListLiveData.value = imagesList
    }

    fun getImages(): MutableList<String>? {
        return imagesListLiveData.value
    }
}