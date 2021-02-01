package ru.behetem.viewmodels

import android.app.Application
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import androidx.lifecycle.Observer
import ru.behetem.R
import ru.behetem.interfaces.IInterestClick
import ru.behetem.interfaces.INationalityClick
import ru.behetem.models.InterestModel
import ru.behetem.models.NationalityModel
import ru.behetem.models.UserModel
import ru.behetem.repositories.InterestsRepository
import ru.behetem.repositories.NationalitiesRepository
import ru.behetem.repositories.UserRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.*

class EditProfileViewModel(application: Application) : BaseAndroidViewModel(application),
    IInterestClick, INationalityClick {

    private val userProfileLiveData: MutableLiveData<UserModel> = MutableLiveData()
    private val showDatePicker: MutableLiveData<Boolean> = MutableLiveData()
    private var selectedLevelPosition: MutableLiveData<Int> = MutableLiveData(0)

    private val interestsList: MutableLiveData<MutableList<InterestModel>> = MutableLiveData()
    private lateinit var interestsApiResponse: LiveData<BaseResponse>
    private lateinit var interestsObserveResponse: Observer<BaseResponse>

    private val nationalitiesList: MutableLiveData<MutableList<NationalityModel>> = MutableLiveData()
    private lateinit var nationalitiesApiResponse: LiveData<BaseResponse>
    private lateinit var nationalitiesObserveResponse: Observer<BaseResponse>

    private val baseResponse: MutableLiveData<BaseResponse> = MutableLiveData()
    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    private lateinit var editProfileApiResponse: LiveData<BaseResponse>
    private lateinit var editProfileObserveResponse: Observer<BaseResponse>
    private val allowToGoBack: MutableLiveData<Boolean> = MutableLiveData()

    private val imagesListLiveData: MutableLiveData<MutableList<String>> = MutableLiveData()

    fun updateProfile(): Boolean {
        return if(userProfileLiveData.value != null) {
            when(userProfileLiveData.value!!.validateEditProfile()) {
                EditProfileErrorConstants.NAME_EMPTY -> {
                    errorResId.value = R.string.enter_name
                    false
                }
                EditProfileErrorConstants.DOB_EMPTY -> {
                    errorResId.value = R.string.choose_dob
                    false
                }
                EditProfileErrorConstants.AGE_LESS -> {
                    errorResId.value = R.string.at_least_18
                    false
                }
                EditProfileErrorConstants.INTEREST_EMPTY -> {
                    errorResId.value = R.string.choose_interests
                    false
                }
                EditProfileErrorConstants.ABOUT_ME_EMPTY -> {
                    errorResId.value = R.string.enter_about_me
                    false
                }
                EditProfileErrorConstants.GROWTH_EMPTY -> {
                    errorResId.value = R.string.enter_growth
                    false
                }
                EditProfileErrorConstants.WEIGHT_EMPTY -> {
                    errorResId.value = R.string.enter_weight
                    false
                }
                EditProfileErrorConstants.NATIONALITY_EMPTY -> {
                    errorResId.value = R.string.choose_nationality
                    false
                }
                EditProfileErrorConstants.POSITION_EMPTY -> {
                    errorResId.value = R.string.enter_position
                    false
                }
                EditProfileErrorConstants.COMPANY_NAME_EMPTY -> {
                    errorResId.value = R.string.enter_company_name
                    false
                }
                EditProfileErrorConstants.INSTITUTE_NAME_EMPTY -> {
                    errorResId.value = R.string.enter_institute_name
                    false
                }
                EditProfileErrorConstants.LEVEL_EMPTY -> {
                    errorResId.value = R.string.enter_level
                    false
                }
                EditProfileErrorConstants.YEAR_EMPTY -> {
                    errorResId.value = R.string.enter_graduation_year
                    false
                }
                EditProfileErrorConstants.YEAR_INVALID -> {
                    errorResId.value = R.string.invalid_graduation_year
                    false
                }
                else -> {
                    loaderVisible.value = true // show loader
                    editProfileObserveResponse = Observer<BaseResponse> { response ->
                        loaderVisible.value = false
                        if(validateResponseWithoutPopup(response)) {
                            allowToGoBack.value = true
                        } else {
                            baseResponse.value = response
                        }
                    }

                    // token
                    val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
                    editProfileApiResponse = UserRepository.changeInfo(userProfileLiveData.value!!, strToken)
                    editProfileApiResponse.observeForever(editProfileObserveResponse)
                    false
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

    fun getNationalities() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            nationalitiesObserveResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
                    if (it.data is MutableList<*>) {
                        val gson = Gson()
                        val strResponse = gson.toJson(it.data)
                        val myType = object : TypeToken<MutableList<String>>() {}.type
                        val nationalitiesStringList: MutableList<String> = gson.fromJson<MutableList<String>>(strResponse, myType)

                        val tempNationalitiesList = ArrayList<NationalityModel>()

                        for(nationality in nationalitiesStringList) {
                            if(userProfileLiveData.value != null && userProfileLiveData.value!!.nationality != null) {
                                if(userProfileLiveData.value!!.nationality!!.equals(nationality, ignoreCase = true)) {
                                    tempNationalitiesList.add(NationalityModel(nationality, true))
                                } else {
                                    tempNationalitiesList.add(NationalityModel(nationality, false))
                                }
                            } else {
                                tempNationalitiesList.add(NationalityModel(nationality, false))
                            }
                        }

                        nationalitiesList.value = tempNationalitiesList
                    }
                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            nationalitiesApiResponse = NationalitiesRepository.getNationalities(strToken)
            nationalitiesApiResponse.observeForever(nationalitiesObserveResponse)
        } else {
            showNoInternet.value = true
        }
    }

    override fun interestItemClicked(view: View, interestItem: InterestModel) {
        interestItem.isSelected = interestItem.isSelected == null || !interestItem.isSelected!!
        interestsList.value = interestsList.value
        if(userProfileLiveData.value != null) {
            if(userProfileLiveData.value!!.interestLabels == null) {
                userProfileLiveData.value?.interestLabels = ArrayList()
            } else {
                userProfileLiveData.value?.interestLabels?.clear()
            }
            if(interestsList.value != null) {
                for(interest in interestsList.value!!) {
                    if(interest.isSelected!!) {
                        userProfileLiveData.value?.interestLabels?.add(interest.label!!)
                    }
                }
            }
        }
    }

    override fun nationalityItemClicked(view: View, nationalityItem: NationalityModel) {
        nationalitiesList.value?.let {
            for(nationality in it) {
                nationality.isSelected = false
            }
        }
        nationalityItem.isSelected = true
        userProfileLiveData.value?.nationality = nationalityItem.label
        nationalitiesList.value = nationalitiesList.value
        userProfileLiveData.value = userProfileLiveData.value
    }

    override fun onCleared() {
        super.onCleared()
        if (this::interestsApiResponse.isInitialized) {
            interestsApiResponse.removeObserver(interestsObserveResponse)
        }
        if (this::nationalitiesApiResponse.isInitialized) {
            nationalitiesApiResponse.removeObserver(nationalitiesObserveResponse)
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

    fun getNationalitiesList(): LiveData<MutableList<NationalityModel>> {
        return nationalitiesList
    }

    fun getErrorResId(): LiveData<Int> {
        return errorResId
    }

    fun setErrorResId(error: Int?) {
        errorResId.value = error
    }

    fun getAllowToGoBack(): LiveData<Boolean> {
        return allowToGoBack
    }

    fun setImagesListLiveData(imagesList: MutableList<String>) {
        imagesListLiveData.value = imagesList
    }

    fun getImagesListLiveData(): LiveData<MutableList<String>> {
        return imagesListLiveData
    }

    fun getImages(): MutableList<String>? {
        return imagesListLiveData.value
    }
}