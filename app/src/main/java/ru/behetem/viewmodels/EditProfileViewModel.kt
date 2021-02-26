package ru.behetem.viewmodels

import android.app.Application
import android.content.DialogInterface
import android.net.Uri
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
import ru.behetem.models.ImageModel
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

    private val openImagePicker: MutableLiveData<Boolean> = MutableLiveData()
    private var currentImageForPosition = -1

    private lateinit var deleteImageApiResponse: LiveData<BaseResponse>
    private lateinit var deleteImageObserveResponse: Observer<BaseResponse>

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

    fun onDeleteClick(view: View, position: Int) {
        if(imagesListLiveData.value != null && imagesListLiveData.value!!.size > position) {
            showAlertDialog(view.context,
                null,
                context.getString(R.string.sure_delete_image),
                context.getString(R.string.yes),
                DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.cancel()
                    deleteImage(position)
                },
                context.getString(R.string.no),
                null
            )
        }
    }

    fun onImageClick(view: View, position: Int) {
        currentImageForPosition = position
        openImagePicker.value = true
    }

    fun setImageUri(uri: Uri) {
//        uploadImage(uri)
    }
    
    private fun deleteImage(position: Int) {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            deleteImageObserveResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
                    imagesListLiveData.value!![position] = ""
                    imagesListLiveData.value = imagesListLiveData.value
                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            val imageModel = ImageModel(position)
            deleteImageApiResponse = UserRepository.deleteImage(strToken, imageModel)
            deleteImageApiResponse.observeForever(deleteImageObserveResponse)
        } else {
            showNoInternet.value = true
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
                            if(userProfileLiveData.value != null && userProfileLiveData.value!!.interests != null) {
                                var isSaved = false
                                for(savedInterest in userProfileLiveData.value!!.interests!!) {
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
                        val myType = object : TypeToken<MutableList<NationalityModel>>() {}.type
                        val nationalities: MutableList<NationalityModel> = gson.fromJson<MutableList<NationalityModel>>(strResponse, myType)

                        for(nationality in nationalities) {
                            if(userProfileLiveData.value != null && userProfileLiveData.value!!.culturalInfo != null && userProfileLiveData.value!!.culturalInfo!!.nationality != null) {
                                if(nationality.ifNationalityMatches(userProfileLiveData.value!!.culturalInfo!!.nationality!!)) {
                                    nationality.isSelected = true
                                    userProfileLiveData.value!!.culturalInfo!!.nationality = nationality.label
                                    userProfileLiveData.value!!.nationalityToShow = nationality.getLabelToShow(getChosenGender())
                                } else{
                                    nationality.isSelected = false
                                }
                            } else {
                                nationality.isSelected =  false
                            }
                        }

                        nationalitiesList.value = nationalities
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
            if(userProfileLiveData.value!!.interests == null) {
                userProfileLiveData.value?.interests = ArrayList()
            } else {
                userProfileLiveData.value?.interests?.clear()
            }
            if(interestsList.value != null) {
                for(interest in interestsList.value!!) {
                    if(interest.isSelected!!) {
                        userProfileLiveData.value?.interests?.add(interest.label!!)
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
        userProfileLiveData.value?.culturalInfo?.nationality = nationalityItem.label
        userProfileLiveData.value?.nationalityToShow = nationalityItem.getLabelToShow(getChosenGender())
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
        if (this::deleteImageApiResponse.isInitialized) {
            deleteImageApiResponse.removeObserver(deleteImageObserveResponse)
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
            it.bodyInfo?.growth = progress
        }
        userProfileLiveData.value = userProfileLiveData.value
    }

    fun onWeightChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        userProfileLiveData.value?.let {
            it.bodyInfo?.weight = progress
        }
        userProfileLiveData.value = userProfileLiveData.value
    }

    fun onPositionTextChanged(charSequence: CharSequence) {
        userProfileLiveData.value?.careerInfo?.workPosition = charSequence.toString()
    }

    fun onCompanyTextChanged(charSequence: CharSequence) {
        userProfileLiveData.value?.careerInfo?.companyName = charSequence.toString()
    }

    fun onInstitutionNameTextChanged(charSequence: CharSequence) {
        userProfileLiveData.value?.careerInfo?.institutionName = charSequence.toString()
    }

    fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                userProfileLiveData.value?.careerInfo?.educationLevel = null
            }
            1 -> {
                userProfileLiveData.value?.careerInfo?.educationLevel = EducationLevels.GENERAL
            }
            2 -> {
                userProfileLiveData.value?.careerInfo?.educationLevel = EducationLevels.SECONDARY
            }
            3 -> {
                userProfileLiveData.value?.careerInfo?.educationLevel = EducationLevels.SPECIALIZED_SECONDARY
            }
            4 -> {
                userProfileLiveData.value?.careerInfo?.educationLevel = EducationLevels.INCOMPLETE_HIGHER
            }
            else -> {
                userProfileLiveData.value?.careerInfo?.educationLevel = EducationLevels.HIGHER
            }
        }
        selectedLevelPosition.value = position
    }

    fun onGraduationYearTextChanged(charSequence: CharSequence) {
        try {
            userProfileLiveData.value?.careerInfo?.graduationYear =
                charSequence.toString().toInt()
        } catch (e: Exception) {
            userProfileLiveData.value?.careerInfo?.graduationYear = 0
        }
    }

    fun getUserProfileLiveData(): LiveData<UserModel> {
        return userProfileLiveData
    }

    fun setCurrentUser(userModel: UserModel) {
        userProfileLiveData.value = userModel
        if (userProfileLiveData.value != null) {
            when (userProfileLiveData.value!!.careerInfo?.educationLevel) {
                EducationLevels.GENERAL -> {
                    selectedLevelPosition.value = 1
                }
                EducationLevels.SECONDARY -> {
                    selectedLevelPosition.value = 2
                }
                EducationLevels.SPECIALIZED_SECONDARY -> {
                    selectedLevelPosition.value = 3
                }
                EducationLevels.INCOMPLETE_HIGHER -> {
                    selectedLevelPosition.value = 4
                }
                EducationLevels.HIGHER -> {
                    selectedLevelPosition.value = 5
                }
            }
        }
    }

    fun getChosenGender(): String {
        return if(this.userProfileLiveData.value != null && this.userProfileLiveData.value!!.gender != null) {
            this.userProfileLiveData.value!!.gender!!
        } else {
            ""
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

    fun getOpenImagePicker(): LiveData<Boolean> {
        return openImagePicker
    }

    fun setOpenImagePicker(open: Boolean) {
        openImagePicker.value = open
    }

    fun getCurrentImageForPosition(): Int {
        return currentImageForPosition
    }

    fun getBaseResponse(): LiveData<BaseResponse?> {
        return baseResponse
    }

    fun setBaseResponse(baseResponse: BaseResponse?) {
        this.baseResponse.value = baseResponse
    }
}