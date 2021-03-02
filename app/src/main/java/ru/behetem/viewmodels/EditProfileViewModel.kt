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
    private val imagesListLiveData: MutableLiveData<MutableList<String>> = MutableLiveData()

    private val showDatePicker: MutableLiveData<Boolean> = MutableLiveData()

    // Spinner selected positions
    private val sFamilyStatusSelection: MutableLiveData<Int> = MutableLiveData(0)
    private val sChildrenPresenceSelection: MutableLiveData<Int> = MutableLiveData(0)
    private val sChildrenDesireSelection: MutableLiveData<Int> = MutableLiveData(0)
    private val sNationalitySelection: MutableLiveData<Int> = MutableLiveData(0)
    private val sAttitudeTowardsTraditionSelection: MutableLiveData<Int> = MutableLiveData(0)
    private val sBodyTypeSelection: MutableLiveData<Int> = MutableLiveData(0)
    private val sEducationLevelSelection: MutableLiveData<Int> = MutableLiveData(0)

    private val openImagePicker: MutableLiveData<Boolean> = MutableLiveData()
    private var currentImageForPosition = -1

    private val interestsList: MutableLiveData<MutableList<InterestModel>> = MutableLiveData()
    private lateinit var interestsApiResponse: LiveData<BaseResponse>
    private lateinit var interestsObserveResponse: Observer<BaseResponse>

    private val nationalitiesList: MutableLiveData<MutableList<NationalityModel>> =
        MutableLiveData()
    private lateinit var nationalitiesApiResponse: LiveData<BaseResponse>
    private lateinit var nationalitiesObserveResponse: Observer<BaseResponse>

    private val errorResId: MutableLiveData<Int> = MutableLiveData()

    private lateinit var editProfileApiResponse: LiveData<BaseResponse>
    private lateinit var editProfileObserveResponse: Observer<BaseResponse>
    private val allowToGoBack: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var deleteImageApiResponse: LiveData<BaseResponse>
    private lateinit var deleteImageObserveResponse: Observer<BaseResponse>

    private val showGrowthWeightBottomSheet: MutableLiveData<Boolean> = MutableLiveData()
    private val showCareerBottomSheet: MutableLiveData<Boolean> = MutableLiveData()

    fun updateProfile(): Boolean {
        return if (userProfileLiveData.value != null) {
            when (userProfileLiveData.value!!.validateEditProfile()) {
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
                        if (validateResponseWithoutPopup(response)) {
                            allowToGoBack.value = true
                        } else {
                            baseResponse.value = response
                        }
                    }

                    // token
                    val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
                    editProfileApiResponse =
                        UserRepository.changeInfo(userProfileLiveData.value!!, strToken)
                    editProfileApiResponse.observeForever(editProfileObserveResponse)
                    false
                }
            }
        } else {
            true
        }
    }

    fun onDeleteClick(view: View, position: Int) {
        if (imagesListLiveData.value != null && imagesListLiveData.value!!.size > position) {
            showAlertDialog(
                view.context,
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
                        val myType = object : TypeToken<MutableList<InterestModel>>() {}.type
                        val interests: MutableList<InterestModel> =
                            gson.fromJson<MutableList<InterestModel>>(strResponse, myType)

                        if (userProfileLiveData.value?.interestsToShow == null) {
                            userProfileLiveData.value?.interestsToShow = ArrayList()
                            for (i in userProfileLiveData.value!!.interests!!) {
                                userProfileLiveData.value?.interestsToShow?.add(i)
                            }
                        }
                        userProfileLiveData.value?.interests = ArrayList()

                        for (interest in interests) {
                            if (userProfileLiveData.value != null && userProfileLiveData.value!!.interests != null) {

                                var isSaved = false
                                for (savedInterest in userProfileLiveData.value!!.interestsToShow!!) {
                                    if (interest.label.equals(savedInterest, ignoreCase = true)) {
                                        isSaved = true
                                        userProfileLiveData.value?.interests?.add(interest.interestId!!)
                                        break
                                    }
                                }

                                interest.isSelected = isSaved
                            } else {
                                interest.isSelected = false
                            }
                        }

                        printLog("after interestsToShow ${userProfileLiveData.value?.interestsToShow}")
                        printLog("after interests ${userProfileLiveData.value?.interests}")

                        interestsList.value = interests
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
                        val nationalities: MutableList<NationalityModel> =
                            gson.fromJson<MutableList<NationalityModel>>(strResponse, myType)

                        nationalities.forEachIndexed { index, nationality ->
                            if (userProfileLiveData.value != null && userProfileLiveData.value!!.culturalInfo != null && userProfileLiveData.value!!.culturalInfo!!.nationality != null) {
                                if (userProfileLiveData.value!!.nationalityToShow == null) {
                                    userProfileLiveData.value!!.nationalityToShow =
                                        userProfileLiveData.value!!.culturalInfo!!.nationality
                                }

                                if (nationality.ifNationalityMatches(userProfileLiveData.value!!.nationalityToShow!!)) {
                                    nationality.isSelected = true
                                    sNationalitySelection.value = index
                                    userProfileLiveData.value!!.culturalInfo!!.nationality =
                                        nationality.nationalityId
                                } else {
                                    nationality.isSelected = false
                                }
                            } else {
                                nationality.isSelected = false
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
        if (userProfileLiveData.value != null) {
            if (userProfileLiveData.value!!.interests == null) {
                userProfileLiveData.value?.interests = ArrayList()
            } else {
                userProfileLiveData.value?.interests?.clear()
            }
            if (userProfileLiveData.value!!.interestsToShow == null) {
                userProfileLiveData.value?.interestsToShow = ArrayList()
            } else {
                userProfileLiveData.value?.interestsToShow?.clear()
            }
            if (interestsList.value != null) {
                for (interest in interestsList.value!!) {
                    if (interest.isSelected!!) {
                        userProfileLiveData.value?.interests?.add(interest.interestId!!)
                        userProfileLiveData.value?.interestsToShow?.add(interest.label!!)
                    }
                }
            }
        }
    }

    override fun nationalityItemClicked(view: View, nationalityItem: NationalityModel) {
        nationalitiesList.value?.let {
            for (nationality in it) {
                nationality.isSelected = false
            }
        }
        nationalityItem.isSelected = true
        userProfileLiveData.value?.culturalInfo?.nationality = nationalityItem.nationalityId
        userProfileLiveData.value?.nationalityToShow =
            nationalityItem.getLabelToShow(getChosenGender())
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

    fun growthWeightClicked(view: View) {
        showGrowthWeightBottomSheet.value = true
    }

    fun careerClicked(view: View) {
        showCareerBottomSheet.value = true
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

    fun onFamilyStatusSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                userProfileLiveData.value?.familyInfo?.status = FamilyStatus.SINGLE
            }
            1 -> {
                userProfileLiveData.value?.familyInfo?.status = FamilyStatus.DIVORCED
            }
            2 -> {
                userProfileLiveData.value?.familyInfo?.status = FamilyStatus.WIDOWED
            }
        }
        sFamilyStatusSelection.value = position
    }

    fun onChildrenPresenceSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                userProfileLiveData.value?.familyInfo?.childrenPresence = ChildrenPresence.NONE
            }
            1 -> {
                userProfileLiveData.value?.familyInfo?.childrenPresence = ChildrenPresence.TOGETHER
            }
            2 -> {
                userProfileLiveData.value?.familyInfo?.childrenPresence = ChildrenPresence.APART
            }
        }
        sChildrenPresenceSelection.value = position
    }

    fun onChildrenDesireSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                userProfileLiveData.value?.familyInfo?.childrenDesire = true
            }
            1 -> {
                userProfileLiveData.value?.familyInfo?.childrenDesire = false
            }
        }
        sChildrenDesireSelection.value = position
    }

    fun onNationalitySelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        userProfileLiveData.value?.culturalInfo?.nationality = nationalitiesList.value!![position].nationalityId
        userProfileLiveData.value?.nationalityToShow = nationalitiesList.value!![position].label
        sNationalitySelection.value = position
    }

    fun onAttitudeTowardsTraditionSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        when (position) {
            0 -> {
                userProfileLiveData.value?.culturalInfo?.traditionsRespect =
                    TraditionsRespect.DONT_KNOW
            }
            1 -> {
                userProfileLiveData.value?.culturalInfo?.traditionsRespect =
                    TraditionsRespect.KNOW_NOT_RESPECT
            }
            2 -> {
                userProfileLiveData.value?.culturalInfo?.traditionsRespect =
                    TraditionsRespect.KNOW_RESPECT
            }
        }
        sAttitudeTowardsTraditionSelection.value = position
    }

    fun onBodyTypeSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                userProfileLiveData.value?.bodyInfo?.bodyType = null
            }
            1 -> {
                userProfileLiveData.value?.bodyInfo?.bodyType = BodyType.THIN
            }
            2 -> {
                userProfileLiveData.value?.bodyInfo?.bodyType = BodyType.SLIM
            }
            3 -> {
                userProfileLiveData.value?.bodyInfo?.bodyType = BodyType.ATHLETIC
            }
            4 -> {
                userProfileLiveData.value?.bodyInfo?.bodyType = BodyType.PLUMP
            }
            5 -> {
                userProfileLiveData.value?.bodyInfo?.bodyType = BodyType.FAT
            }
        }
        sBodyTypeSelection.value = position
    }

    fun onEducationLevelSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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
                userProfileLiveData.value?.careerInfo?.educationLevel =
                    EducationLevels.SPECIALIZED_SECONDARY
            }
            4 -> {
                userProfileLiveData.value?.careerInfo?.educationLevel =
                    EducationLevels.INCOMPLETE_HIGHER
            }
            else -> {
                userProfileLiveData.value?.careerInfo?.educationLevel = EducationLevels.HIGHER
            }
        }
        sEducationLevelSelection.value = position
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
            when (userProfileLiveData.value!!.familyInfo?.status) {
                FamilyStatus.SINGLE -> {
                    sFamilyStatusSelection.value = 0
                }
                FamilyStatus.DIVORCED -> {
                    sFamilyStatusSelection.value = 1
                }
                FamilyStatus.WIDOWED -> {
                    sFamilyStatusSelection.value = 2
                }
            }

            when (userProfileLiveData.value!!.familyInfo?.childrenPresence) {
                ChildrenPresence.NONE -> {
                    sChildrenPresenceSelection.value = 0
                }
                ChildrenPresence.TOGETHER -> {
                    sChildrenPresenceSelection.value = 1
                }
                ChildrenPresence.APART -> {
                    sChildrenPresenceSelection.value = 2
                }
            }

            if (userProfileLiveData.value!!.familyInfo?.childrenDesire == true) {
                sChildrenDesireSelection.value = 0
            } else {
                sChildrenDesireSelection.value = 1
            }

            when (userProfileLiveData.value!!.culturalInfo?.traditionsRespect) {
                TraditionsRespect.DONT_KNOW -> {
                    sAttitudeTowardsTraditionSelection.value = 0
                }
                TraditionsRespect.KNOW_NOT_RESPECT -> {
                    sAttitudeTowardsTraditionSelection.value = 1
                }
                TraditionsRespect.KNOW_RESPECT -> {
                    sAttitudeTowardsTraditionSelection.value = 2
                }
            }

            when (userProfileLiveData.value!!.bodyInfo?.bodyType) {
                BodyType.THIN -> {
                    sBodyTypeSelection.value = 1
                }
                BodyType.SLIM -> {
                    sBodyTypeSelection.value = 2
                }
                BodyType.ATHLETIC -> {
                    sBodyTypeSelection.value = 3
                }
                BodyType.PLUMP -> {
                    sBodyTypeSelection.value = 4
                }
                BodyType.FAT -> {
                    sBodyTypeSelection.value = 5
                }
                else -> {
                    sBodyTypeSelection.value = 0
                }
            }

            when (userProfileLiveData.value!!.careerInfo?.educationLevel) {
                EducationLevels.GENERAL -> {
                    sEducationLevelSelection.value = 1
                }
                EducationLevels.SECONDARY -> {
                    sEducationLevelSelection.value = 2
                }
                EducationLevels.SPECIALIZED_SECONDARY -> {
                    sEducationLevelSelection.value = 3
                }
                EducationLevels.INCOMPLETE_HIGHER -> {
                    sEducationLevelSelection.value = 4
                }
                EducationLevels.HIGHER -> {
                    sEducationLevelSelection.value = 5
                }
            }
        }
    }

    fun getChosenGender(): String {
        return if (this.userProfileLiveData.value != null && this.userProfileLiveData.value!!.gender != null) {
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

    fun getSEducationLevelSelection(): LiveData<Int> {
        return sEducationLevelSelection
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

    fun setShowGrowthWeightBottomSheet(show: Boolean) {
        showGrowthWeightBottomSheet.value = show
    }

    fun getShowGrowthWeightBottomSheet(): LiveData<Boolean> {
        return showGrowthWeightBottomSheet
    }

    fun setShowCareerBottomSheet(show: Boolean) {
        showCareerBottomSheet.value = show
    }

    fun getShowCareerBottomSheet(): LiveData<Boolean> {
        return showCareerBottomSheet
    }

    fun getSFamilyStatusSelection(): LiveData<Int> {
        return sFamilyStatusSelection
    }

    fun getSChildrenPresenceSelection(): LiveData<Int> {
        return sChildrenPresenceSelection
    }

    fun getSChildrenDesireSelection(): LiveData<Int> {
        return sChildrenDesireSelection
    }

    fun getSNationalitySelection(): LiveData<Int> {
        return sNationalitySelection
    }

    fun getSAttitudeTowardsTraditionSelection(): LiveData<Int> {
        return sAttitudeTowardsTraditionSelection
    }

    fun getSBodyTypeSelection(): LiveData<Int> {
        return sBodyTypeSelection
    }
}