package com.example.dating.viewmodels

import android.app.Application
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dating.R
import com.example.dating.models.InterestModel
import com.example.dating.models.UserModel
import com.example.dating.repositories.InterestsRepository
import com.example.dating.repositories.UserRepository
import com.example.dating.responses.BaseResponse
import com.example.dating.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import androidx.lifecycle.Observer
import com.example.dating.interfaces.IInterestClick

class EditProfileViewModel(application: Application) : BaseAndroidViewModel(application), IInterestClick {

    private val userProfileLiveData: MutableLiveData<UserModel> = MutableLiveData()
    private val showDatePicker: MutableLiveData<Boolean> = MutableLiveData()
    private var selectedLevelPosition: MutableLiveData<Int> = MutableLiveData(0)

    private val interestsList: MutableLiveData<MutableList<InterestModel>> = MutableLiveData()
    private lateinit var interestsApiResponse: LiveData<BaseResponse>
    private lateinit var interestsObserveResponse: Observer<BaseResponse>

    private val baseResponse: MutableLiveData<BaseResponse> = MutableLiveData()

    fun updateProfile(): Boolean {
        return if(userProfileLiveData.value != null) {
            when(userProfileLiveData.value!!.validateEditProfile()) {
                1 -> {
                    false
                }
                else -> {
                    // token
                    val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
                    UserRepository.changeInfo(userProfileLiveData.value!!, strToken)
                    true
                }
            }
        } else {
            true
        }
    }

    fun getInterests() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            interestsObserveResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
                    if (it.data is MutableList<*>) {
                        val gson = Gson()
                        val strResponse = gson.toJson(it.data)
                        val myType = object : TypeToken<MutableList<String>>() {}.type
                        val interestStringList: MutableList<String> =
                            gson.fromJson<MutableList<String>>(strResponse, myType)

                        val tempInterestsList = ArrayList<InterestModel>()

                        for (interest in interestStringList) {
                            if(userProfileLiveData.value != null && userProfileLiveData.value!!.interestLabels != null) {
                                var isSaved = false
                                for(savedInterest in userProfileLiveData.value!!.interestLabels!!) {
                                    if(interest.equals(savedInterest, ignoreCase = true)) {
                                        isSaved = true
                                        break
                                    }
                                }
                                if(isSaved) {
                                    tempInterestsList.add(InterestModel(interest, true))
                                } else {
                                    tempInterestsList.add(InterestModel(interest, false))
                                }
                            } else {
                                tempInterestsList.add(InterestModel(interest, false))
                            }
                        }

                        interestsList.value = tempInterestsList
                    }
                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            interestsApiResponse = InterestsRepository.getInterests(strToken)
            interestsApiResponse.observeForever(interestsObserveResponse)
        } else {
            showNoInternet.value = true
        }
    }

    override fun interestItemClicked(view: View, interestItem: InterestModel) {

    }

    override fun onCleared() {
        super.onCleared()
        if (this::interestsApiResponse.isInitialized) {
            interestsApiResponse.removeObserver(interestsObserveResponse)
        }
    }

    fun onMaleClicked(view: View) {
        userProfileLiveData.value?.gender = Gender.MALE
        userProfileLiveData.value = userProfileLiveData.value
    }

    fun onFemaleClicked(view: View) {
        userProfileLiveData.value?.gender = Gender.FEMALE
        userProfileLiveData.value = userProfileLiveData.value
    }

    fun onNameTextChanged(charSequence: CharSequence) {
        userProfileLiveData.value?.name = charSequence.toString()
    }

    fun onDobClicked(view: View) {
        showDatePicker.value = true
    }

    fun setDateOfBirth(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)

        userProfileLiveData.value?.selectedDOB = calendar
        userProfileLiveData.value?.dateOfBirth = formatDobForAPI(calendar)
        userProfileLiveData.value = userProfileLiveData.value
    }

    fun onAboutMeTextChanged(charSequence: CharSequence) {
        userProfileLiveData.value?.let {
            it.description = charSequence.toString()
        }
    }

    fun onGrowthChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        userProfileLiveData.value?.let {
            it.growth = progress
        }
        userProfileLiveData.value = userProfileLiveData.value
    }

    fun onWeightChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        userProfileLiveData.value?.let {
            it.weight = progress
        }
        userProfileLiveData.value = userProfileLiveData.value
    }

    fun onPositionTextChanged(charSequence: CharSequence) {
        userProfileLiveData.value?.workInfo?.position = charSequence.toString()
    }

    fun onCompanyTextChanged(charSequence: CharSequence) {
        userProfileLiveData.value?.workInfo?.companyName = charSequence.toString()
    }

    fun onInstitutionNameTextChanged(charSequence: CharSequence) {
        userProfileLiveData.value?.educationInfo?.institutionName = charSequence.toString()
    }

    fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val array = view?.context?.resources?.getStringArray(R.array.education_level)
        when (position) {
            0 -> {
                userProfileLiveData.value?.educationInfo?.level = null
            }
            1 -> {
                userProfileLiveData.value?.educationInfo?.level = array?.get(1)
            }
            else -> {
                userProfileLiveData.value?.educationInfo?.level = array?.get(2)
            }
        }
        selectedLevelPosition.value = position
    }

    fun onGraduationYearTextChanged(charSequence: CharSequence) {
        try {
            userProfileLiveData.value?.educationInfo?.graduationYear =
                charSequence.toString().toInt()
        } catch (e: Exception) {
            userProfileLiveData.value?.educationInfo?.graduationYear = 0
        }
    }

    fun getUserProfileLiveData(): LiveData<UserModel> {
        return userProfileLiveData
    }

    fun setCurrentUser(userModel: UserModel) {
        userProfileLiveData.value = userModel
        if (userProfileLiveData.value != null) {
            if (userProfileLiveData.value!!.educationInfo?.level == EducationLevels.GENERAL) {
                selectedLevelPosition.value = 1
            } else if (userProfileLiveData.value!!.educationInfo?.level == EducationLevels.HIGH) {
                selectedLevelPosition.value = 2
            }
        }
    }

    fun getCurrentUser(): UserModel? {
        return userProfileLiveData.value
    }

    fun getShowDatePicker(): LiveData<Boolean> {
        return showDatePicker
    }

    fun setShowDatePicker(show: Boolean) {
        showDatePicker.value = show
    }

    fun getSelectedLevelPosition(): LiveData<Int> {
        return selectedLevelPosition
    }

    fun getInterestsList(): LiveData<MutableList<InterestModel>> {
        return interestsList
    }
}