package ru.behetem.viewmodels

import android.app.Application
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ru.behetem.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.behetem.R
import ru.behetem.interfaces.IInterestClick
import ru.behetem.models.*
import ru.behetem.repositories.*
import ru.behetem.responses.BaseResponse
import java.util.*
import kotlin.collections.ArrayList

class ProfilesViewModel(application: Application) : BaseAndroidViewModel(application),
    IInterestClick {

    private var filterModelLiveData: MutableLiveData<FilterModel> = MutableLiveData()

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>

    private lateinit var saveFilterApiResponse: LiveData<BaseResponse>
    private lateinit var saveFilterObserveResponse: Observer<BaseResponse>

    private var shouldHitPagination = true
    private val usersListLiveData: MutableLiveData<MutableList<UserModel>> = MutableLiveData()
    private lateinit var usersApiResponse: LiveData<BaseResponse>
    private lateinit var usersObserveResponse: Observer<BaseResponse>

    private lateinit var locationApiResponse: LiveData<BaseResponse>
    private lateinit var locationObserveResponse: Observer<BaseResponse>

    private val showFiltersLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val interestsList: MutableLiveData<MutableList<InterestModel>> = MutableLiveData()
    private lateinit var interestsApiResponse: LiveData<BaseResponse>
    private lateinit var interestsObserveResponse: Observer<BaseResponse>
    private var selectedDistancePosition: MutableLiveData<Int> = MutableLiveData(0)
    private val showGrowthWeightBottomSheet: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var getCommercialApiResponse: LiveData<BaseResponse>
    private lateinit var getCommercialObserveResponse: Observer<BaseResponse>

    private fun getFilters() {
        filterModelLiveData.value = getFiltersFromShared(context)
        if (filterModelLiveData.value == null) {
            fetchFilters()
        } else {
            setDistanceLevel()
            getUsers()
        }
    }

    private fun setDistanceLevel() {
        if (filterModelLiveData.value != null) {
            when (filterModelLiveData.value!!.maxDistance) {
                10 -> {
                    selectedDistancePosition.value = 0
                }
                30 -> {
                    selectedDistancePosition.value = 1
                }
                80 -> {
                    selectedDistancePosition.value = 2
                }
                150 -> {
                    selectedDistancePosition.value = 3
                }
                else -> {
                    selectedDistancePosition.value = 4
                }
            }
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
                    setDistanceLevel()
                    getUsers()
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

    fun resetPage() {
        filterModelLiveData.value?.page = 0
        filterModelLiveData.value?.let { filter ->
            saveFiltersToShared(context, filter)
        }
        filterModelLiveData.value = filterModelLiveData.value
    }

    fun saveFilters() {
        if (validateInternet(context)) {
            loaderVisible.value = true // show loader
            saveFilterObserveResponse = Observer<BaseResponse> { response ->
                loaderVisible.value = false
                if (validateResponse(context, response)) {
                    getUsers()
                }
            }
            // token
            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"

            filterModelLiveData.value?.let {
                saveFilterApiResponse = FiltersRepository.saveFilters(it, strToken)
            }
            saveFilterApiResponse.observeForever(saveFilterObserveResponse)
        }
    }

    fun getNextPageUsers() {
        filterModelLiveData.value?.page = filterModelLiveData.value?.page!! + 1
        getUsers()
    }

    fun clearUsers() {
        usersListLiveData.value?.clear()
        usersListLiveData.value = usersListLiveData.value
    }

    private fun getUsers() {
        if (isInternetAvailable(context)) {
            if (usersListLiveData.value == null) {
                usersListLiveData.value = ArrayList()
            }
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            usersObserveResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
                    if (it.data is MutableList<*>) {
                        val gson = Gson()
                        val strResponse = gson.toJson(it.data)
                        val myType = object : TypeToken<MutableList<UserModel>>() {}.type
                        val usersList: MutableList<UserModel> =
                            gson.fromJson<MutableList<UserModel>>(strResponse, myType)

                        for (user in usersList) {
                            val imagesList = ArrayList<String>()
                            user.images?.let { images ->
                                for (image in images) {
                                    val imageUrl = "${ApiConstants.BASE_URL}$image"
                                    imagesList.add(imageUrl)
                                }
                            }
                            user.images = imagesList
                        }

                        usersListLiveData.value?.addAll(usersList)
                        usersListLiveData.value = usersListLiveData.value

                        if (usersList.size < Constants.PAGE_SIZE) {
                            shouldHitPagination = false
                        }
                    }
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

    fun setLatLong(lat: Double, lon: Double) {
        val locationModel = LocationModel()
        locationModel.geometry.coordinates.add(lon)
        locationModel.geometry.coordinates.add(lat)

        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            locationObserveResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
//                    getFilters()
                    getCommercial()
                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            locationApiResponse = LocationRepository.updateLocation(locationModel, strToken)
            locationApiResponse.observeForever(locationObserveResponse)
        } else {
            showNoInternet.value = true
        }
    }

    private fun getCommercial() {
        if (validateInternet(context)) {
            loaderVisible.value = true // show loader
            getCommercialObserveResponse = Observer<BaseResponse> { response ->
                loaderVisible.value = false
                if (validateResponseWithoutPopup(response)) {
                    val gson = Gson()
                    val strResponse = gson.toJson(response.data)
                    val myType = object : TypeToken<CommercialModel>() {}.type
                    val commercialModel: CommercialModel = gson.fromJson<CommercialModel>(strResponse, myType)

                    saveCommercialToShared(context, commercialModel)

                    getFilters()
                } else {
                    baseResponse.value = response
                }
            }
            // token
            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            getCommercialApiResponse = CommercialRepository.getCommercial(strToken)
            getCommercialApiResponse.observeForever(getCommercialObserveResponse)
        }
    }

    private fun getInterests() {
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

                        for (interest in interests) {
                            if (filterModelLiveData.value != null && filterModelLiveData.value!!.interest != null) {
                                var isSaved = false
                                for (savedInterest in filterModelLiveData.value!!.interest!!) {
                                    if (interest.interestId.equals(
                                            savedInterest,
                                            ignoreCase = true
                                        )
                                    ) {
                                        isSaved = true
                                        break
                                    }
                                }
                                interest.isSelected = isSaved
                            } else {
                                interest.isSelected = false
                            }
                        }

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

    fun onFilterClick(view: View) {
        clearUsers()
        if (interestsList.value == null || interestsList.value!!.isEmpty()) {
            getInterests()
        }
        showFiltersLiveData.value = true
    }

    fun onMaleClicked(view: View) {
        filterModelLiveData.value?.gender = Gender.MALE
        filterModelLiveData.value = filterModelLiveData.value
        filterModelLiveData.value?.let {
            saveFiltersToShared(view.context, it)
        }
    }

    fun onFemaleClicked(view: View) {
        filterModelLiveData.value?.gender = Gender.FEMALE
        filterModelLiveData.value = filterModelLiveData.value
        filterModelLiveData.value?.let {
            saveFiltersToShared(view.context, it)
        }
    }

    fun updateAgeFromAndTo(ageFrom: Int, ageTo: Int) {
        filterModelLiveData.value?.ageFrom = ageFrom
        filterModelLiveData.value?.ageTo = ageTo

        val calendarFrom = Calendar.getInstance()
        calendarFrom.set(Calendar.YEAR, calendarFrom.get(Calendar.YEAR) - ageFrom)

        val calendarTo = Calendar.getInstance()
        calendarTo.set(Calendar.YEAR, calendarTo.get(Calendar.YEAR) - ageTo)

        filterModelLiveData.value?.dateOfBirthFrom = formatDobForAPI(calendarFrom)
        filterModelLiveData.value?.dateOfBirthTo = formatDobForAPI(calendarTo)

        filterModelLiveData.value?.let {
            saveFiltersToShared(context, it)
        }
        filterModelLiveData.value = filterModelLiveData.value
    }

    fun updateGrowthFromAndTo(growthFrom: Int, growthTo: Int) {
        filterModelLiveData.value?.growthFrom = growthFrom
        filterModelLiveData.value?.growthTo = growthTo

        filterModelLiveData.value?.let {
            saveFiltersToShared(context, it)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun updateWeightFromAndTo(weightFrom: Int, weightTo: Int) {
        filterModelLiveData.value?.weightFrom = weightFrom
        filterModelLiveData.value?.weightTo = weightTo

        filterModelLiveData.value?.let {
            saveFiltersToShared(context, it)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                filterModelLiveData.value?.maxDistance = 10
            }
            1 -> {
                filterModelLiveData.value?.maxDistance = 30
            }
            2 -> {
                filterModelLiveData.value?.maxDistance = 80
            }
            3 -> {
                filterModelLiveData.value?.maxDistance = 150
            }
            else -> {
                filterModelLiveData.value?.maxDistance = null
            }
        }
        selectedDistancePosition.value = position
    }

    override fun interestItemClicked(view: View, interestItem: InterestModel) {
        interestItem.isSelected = interestItem.isSelected == null || !interestItem.isSelected!!
        interestsList.value = interestsList.value
        if (filterModelLiveData.value != null) {
            if (filterModelLiveData.value!!.interest == null) {
                filterModelLiveData.value?.interest = ArrayList()
            } else {
                filterModelLiveData.value?.interest?.clear()
            }
            if (interestsList.value != null) {
                for (interest in interestsList.value!!) {
                    if (interest.isSelected!!) {
                        filterModelLiveData.value?.interest?.add(interest.interestId!!)
                    }
                }
            }
        }
    }

    fun growthWeightClicked(view: View) {
        showGrowthWeightBottomSheet.value = true
    }

    fun onSingleClicked(view: View) {
        if(filterModelLiveData.value!!.status == null) {
            filterModelLiveData.value!!.status = ArrayList()
        }
        if (filterModelLiveData.value!!.status!!.contains(FamilyStatus.SINGLE)) {
            filterModelLiveData.value!!.status!!.remove(FamilyStatus.SINGLE)
        } else {
            filterModelLiveData.value!!.status!!.add(FamilyStatus.SINGLE)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isSingleChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.status == null) {
            false
        } else {
            filterModelLiveData.value!!.status!!.contains(FamilyStatus.SINGLE)
        }
    }

    fun onDivorcedClicked(view: View) {
        if(filterModelLiveData.value!!.status == null) {
            filterModelLiveData.value!!.status = ArrayList()
        }
        if (filterModelLiveData.value!!.status!!.contains(FamilyStatus.DIVORCED)) {
            filterModelLiveData.value!!.status!!.remove(FamilyStatus.DIVORCED)
        } else {
            filterModelLiveData.value!!.status!!.add(FamilyStatus.DIVORCED)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isDivorcedChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.status == null) {
            false
        } else {
            filterModelLiveData.value!!.status!!.contains(FamilyStatus.DIVORCED)
        }
    }

    fun onWidowedClicked(view: View) {
        if(filterModelLiveData.value!!.status == null) {
            filterModelLiveData.value!!.status = ArrayList()
        }
        if (filterModelLiveData.value!!.status!!.contains(FamilyStatus.WIDOWED)) {
            filterModelLiveData.value!!.status!!.remove(FamilyStatus.WIDOWED)
        } else {
            filterModelLiveData.value!!.status!!.add(FamilyStatus.WIDOWED)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isWidowedChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.status == null) {
            false
        } else {
            filterModelLiveData.value!!.status!!.contains(FamilyStatus.WIDOWED)
        }
    }

    fun onIDontKnowTraditionClicked(view: View) {
        if(filterModelLiveData.value!!.traditionsRespect == null) {
            filterModelLiveData.value!!.traditionsRespect = ArrayList()
        }
        if (filterModelLiveData.value!!.traditionsRespect!!.contains(TraditionsRespect.DONT_KNOW)) {
            filterModelLiveData.value!!.traditionsRespect!!.remove(TraditionsRespect.DONT_KNOW)
        } else {
            filterModelLiveData.value!!.traditionsRespect!!.add(TraditionsRespect.DONT_KNOW)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isIDontKnowTraditionChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.traditionsRespect == null) {
            false
        } else {
            filterModelLiveData.value!!.traditionsRespect!!.contains(TraditionsRespect.DONT_KNOW)
        }
    }

    fun onKnowNotRespectTraditionClicked(view: View) {
        if(filterModelLiveData.value!!.traditionsRespect == null) {
            filterModelLiveData.value!!.traditionsRespect = ArrayList()
        }
        if (filterModelLiveData.value!!.traditionsRespect!!.contains(TraditionsRespect.KNOW_NOT_RESPECT)) {
            filterModelLiveData.value!!.traditionsRespect!!.remove(TraditionsRespect.KNOW_NOT_RESPECT)
        } else {
            filterModelLiveData.value!!.traditionsRespect!!.add(TraditionsRespect.KNOW_NOT_RESPECT)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isKnowNotRespectTraditionChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.traditionsRespect == null) {
            false
        } else {
            filterModelLiveData.value!!.traditionsRespect!!.contains(TraditionsRespect.KNOW_NOT_RESPECT)
        }
    }

    fun onKnowRespectTraditionClicked(view: View) {
        if(filterModelLiveData.value!!.traditionsRespect == null) {
            filterModelLiveData.value!!.traditionsRespect = ArrayList()
        }
        if (filterModelLiveData.value!!.traditionsRespect!!.contains(TraditionsRespect.KNOW_RESPECT)) {
            filterModelLiveData.value!!.traditionsRespect!!.remove(TraditionsRespect.KNOW_RESPECT)
        } else {
            filterModelLiveData.value!!.traditionsRespect!!.add(TraditionsRespect.KNOW_RESPECT)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isKnowRespectTraditionChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.traditionsRespect == null) {
            false
        } else {
            filterModelLiveData.value!!.traditionsRespect!!.contains(TraditionsRespect.KNOW_RESPECT)
        }
    }

    fun onSkinnyClicked(view: View) {
        if(filterModelLiveData.value!!.bodyType == null) {
            filterModelLiveData.value!!.bodyType = ArrayList()
        }
        if (filterModelLiveData.value!!.bodyType!!.contains(BodyType.THIN)) {
            filterModelLiveData.value!!.bodyType!!.remove(BodyType.THIN)
        } else {
            filterModelLiveData.value!!.bodyType!!.add(BodyType.THIN)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isSkinnyChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.bodyType == null) {
            false
        } else {
            filterModelLiveData.value!!.bodyType!!.contains(BodyType.THIN)
        }
    }

    fun onSlenderClicked(view: View) {
        if(filterModelLiveData.value!!.bodyType == null) {
            filterModelLiveData.value!!.bodyType = ArrayList()
        }
        if (filterModelLiveData.value!!.bodyType!!.contains(BodyType.SLIM)) {
            filterModelLiveData.value!!.bodyType!!.remove(BodyType.SLIM)
        } else {
            filterModelLiveData.value!!.bodyType!!.add(BodyType.SLIM)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isSlenderChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.bodyType == null) {
            false
        } else {
            filterModelLiveData.value!!.bodyType!!.contains(BodyType.SLIM)
        }
    }

    fun onSportsClicked(view: View) {
        if(filterModelLiveData.value!!.bodyType == null) {
            filterModelLiveData.value!!.bodyType = ArrayList()
        }
        if (filterModelLiveData.value!!.bodyType!!.contains(BodyType.ATHLETIC)) {
            filterModelLiveData.value!!.bodyType!!.remove(BodyType.ATHLETIC)
        } else {
            filterModelLiveData.value!!.bodyType!!.add(BodyType.ATHLETIC)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isSportsChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.bodyType == null) {
            false
        } else {
            filterModelLiveData.value!!.bodyType!!.contains(BodyType.ATHLETIC)
        }
    }

    fun onDenseClicked(view: View) {
        if(filterModelLiveData.value!!.bodyType == null) {
            filterModelLiveData.value!!.bodyType = ArrayList()
        }
        if (filterModelLiveData.value!!.bodyType!!.contains(BodyType.PLUMP)) {
            filterModelLiveData.value!!.bodyType!!.remove(BodyType.PLUMP)
        } else {
            filterModelLiveData.value!!.bodyType!!.add(BodyType.PLUMP)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isDenseChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.bodyType == null) {
            false
        } else {
            filterModelLiveData.value!!.bodyType!!.contains(BodyType.PLUMP)
        }
    }

    fun onCompleteClicked(view: View) {
        if(filterModelLiveData.value!!.bodyType == null) {
            filterModelLiveData.value!!.bodyType = ArrayList()
        }
        if (filterModelLiveData.value!!.bodyType!!.contains(BodyType.FAT)) {
            filterModelLiveData.value!!.bodyType!!.remove(BodyType.FAT)
        } else {
            filterModelLiveData.value!!.bodyType!!.add(BodyType.FAT)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isCompleteChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.bodyType == null) {
            false
        } else {
            filterModelLiveData.value!!.bodyType!!.contains(BodyType.FAT)
        }
    }

    fun onGeneralClicked(view: View) {
        if(filterModelLiveData.value!!.educationLevel == null) {
            filterModelLiveData.value!!.educationLevel = ArrayList()
        }
        if (filterModelLiveData.value!!.educationLevel!!.contains(EducationLevels.GENERAL)) {
            filterModelLiveData.value!!.educationLevel!!.remove(EducationLevels.GENERAL)
        } else {
            filterModelLiveData.value!!.educationLevel!!.add(EducationLevels.GENERAL)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isGeneralChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.educationLevel == null) {
            false
        } else {
            filterModelLiveData.value!!.educationLevel!!.contains(EducationLevels.GENERAL)
        }
    }

    fun onAverageClicked(view: View) {
        if(filterModelLiveData.value!!.educationLevel == null) {
            filterModelLiveData.value!!.educationLevel = ArrayList()
        }
        if (filterModelLiveData.value!!.educationLevel!!.contains(EducationLevels.SECONDARY)) {
            filterModelLiveData.value!!.educationLevel!!.remove(EducationLevels.SECONDARY)
        } else {
            filterModelLiveData.value!!.educationLevel!!.add(EducationLevels.SECONDARY)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isAverageChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.educationLevel == null) {
            false
        } else {
            filterModelLiveData.value!!.educationLevel!!.contains(EducationLevels.SECONDARY)
        }
    }

    fun onSpecializedSecondaryClicked(view: View) {
        if(filterModelLiveData.value!!.educationLevel == null) {
            filterModelLiveData.value!!.educationLevel = ArrayList()
        }
        if (filterModelLiveData.value!!.educationLevel!!.contains(EducationLevels.SPECIALIZED_SECONDARY)) {
            filterModelLiveData.value!!.educationLevel!!.remove(EducationLevels.SPECIALIZED_SECONDARY)
        } else {
            filterModelLiveData.value!!.educationLevel!!.add(EducationLevels.SPECIALIZED_SECONDARY)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isSpecializedSecondaryChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.educationLevel == null) {
            false
        } else {
            filterModelLiveData.value!!.educationLevel!!.contains(EducationLevels.SPECIALIZED_SECONDARY)
        }
    }

    fun onUnfinishedHigherClicked(view: View) {
        if(filterModelLiveData.value!!.educationLevel == null) {
            filterModelLiveData.value!!.educationLevel = ArrayList()
        }
        if (filterModelLiveData.value!!.educationLevel!!.contains(EducationLevels.INCOMPLETE_HIGHER)) {
            filterModelLiveData.value!!.educationLevel!!.remove(EducationLevels.INCOMPLETE_HIGHER)
        } else {
            filterModelLiveData.value!!.educationLevel!!.add(EducationLevels.INCOMPLETE_HIGHER)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isUnfinishedHigherChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.educationLevel == null) {
            false
        } else {
            filterModelLiveData.value!!.educationLevel!!.contains(EducationLevels.INCOMPLETE_HIGHER)
        }
    }

    fun onHigherClicked(view: View) {
        if(filterModelLiveData.value!!.educationLevel == null) {
            filterModelLiveData.value!!.educationLevel = ArrayList()
        }
        if (filterModelLiveData.value!!.educationLevel!!.contains(EducationLevels.HIGHER)) {
            filterModelLiveData.value!!.educationLevel!!.remove(EducationLevels.HIGHER)
        } else {
            filterModelLiveData.value!!.educationLevel!!.add(EducationLevels.HIGHER)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isHigherChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.educationLevel == null) {
            false
        } else {
            filterModelLiveData.value!!.educationLevel!!.contains(EducationLevels.HIGHER)
        }
    }

    fun onNotHavingChildrenClicked(view: View) {
        if(filterModelLiveData.value!!.childrenPresence == null) {
            filterModelLiveData.value!!.childrenPresence = ArrayList()
        }
        if (filterModelLiveData.value!!.childrenPresence!!.contains(ChildrenPresence.NONE)) {
            filterModelLiveData.value!!.childrenPresence!!.remove(ChildrenPresence.NONE)
        } else {
            filterModelLiveData.value!!.childrenPresence!!.add(ChildrenPresence.NONE)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isNotHavingChildrenChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.childrenPresence == null) {
            false
        } else {
            filterModelLiveData.value!!.childrenPresence!!.contains(ChildrenPresence.NONE)
        }
    }

    fun onLiveTogetherClicked(view: View) {
        if(filterModelLiveData.value!!.childrenPresence == null) {
            filterModelLiveData.value!!.childrenPresence = ArrayList()
        }
        if (filterModelLiveData.value!!.childrenPresence!!.contains(ChildrenPresence.TOGETHER)) {
            filterModelLiveData.value!!.childrenPresence!!.remove(ChildrenPresence.TOGETHER)
        } else {
            filterModelLiveData.value!!.childrenPresence!!.add(ChildrenPresence.TOGETHER)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isLiveTogetherChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.childrenPresence == null) {
            false
        } else {
            filterModelLiveData.value!!.childrenPresence!!.contains(ChildrenPresence.TOGETHER)
        }
    }

    fun onLiveSeparateClicked(view: View) {
        if(filterModelLiveData.value!!.childrenPresence == null) {
            filterModelLiveData.value!!.childrenPresence = ArrayList()
        }
        if (filterModelLiveData.value!!.childrenPresence!!.contains(ChildrenPresence.APART)) {
            filterModelLiveData.value!!.childrenPresence!!.remove(ChildrenPresence.APART)
        } else {
            filterModelLiveData.value!!.childrenPresence!!.add(ChildrenPresence.APART)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isLiveSeparateChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.childrenPresence == null) {
            false
        } else {
            filterModelLiveData.value!!.childrenPresence!!.contains(ChildrenPresence.APART)
        }
    }

    fun onYesDesireClicked(view: View) {
        filterModelLiveData.value?.childrenDesire = true
    }

    fun isYesDesire(): Boolean {
        return if(filterModelLiveData.value?.childrenDesire == null) false else filterModelLiveData.value?.childrenDesire!!
    }

    fun onNoDesireClicked(view: View) {
        filterModelLiveData.value?.childrenDesire = false
    }

    fun isNoDesire(): Boolean {
        return if(filterModelLiveData.value?.childrenDesire == null) false else !(filterModelLiveData.value?.childrenDesire!!)
    }

    fun onNonBelieverClicked(view: View) {
        if(filterModelLiveData.value!!.religionRespect == null) {
            filterModelLiveData.value!!.religionRespect = ArrayList()
        }
        if (filterModelLiveData.value!!.religionRespect!!.contains(ReligionRespect.ATHEIST)) {
            filterModelLiveData.value!!.religionRespect!!.remove(ReligionRespect.ATHEIST)
        } else {
            filterModelLiveData.value!!.religionRespect!!.add(ReligionRespect.ATHEIST)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isNonBelieverChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.religionRespect == null) {
            false
        } else {
            filterModelLiveData.value!!.religionRespect!!.contains(ReligionRespect.ATHEIST)
        }
    }

    fun onBelieverClicked(view: View) {
        if(filterModelLiveData.value!!.religionRespect == null) {
            filterModelLiveData.value!!.religionRespect = ArrayList()
        }
        if (filterModelLiveData.value!!.religionRespect!!.contains(ReligionRespect.RELIGIOUS)) {
            filterModelLiveData.value!!.religionRespect!!.remove(ReligionRespect.RELIGIOUS)
        } else {
            filterModelLiveData.value!!.religionRespect!!.add(ReligionRespect.RELIGIOUS)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isBelieverChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.religionRespect == null) {
            false
        } else {
            filterModelLiveData.value!!.religionRespect!!.contains(ReligionRespect.RELIGIOUS)
        }
    }

    fun onCanonicalBelieverClicked(view: View) {
        if(filterModelLiveData.value!!.religionRespect == null) {
            filterModelLiveData.value!!.religionRespect = ArrayList()
        }
        if (filterModelLiveData.value!!.religionRespect!!.contains(ReligionRespect.CANONICAL_RELIGIOUS)) {
            filterModelLiveData.value!!.religionRespect!!.remove(ReligionRespect.CANONICAL_RELIGIOUS)
        } else {
            filterModelLiveData.value!!.religionRespect!!.add(ReligionRespect.CANONICAL_RELIGIOUS)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isCanonicalBelieverChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.religionRespect == null) {
            false
        } else {
            filterModelLiveData.value!!.religionRespect!!.contains(ReligionRespect.CANONICAL_RELIGIOUS)
        }
    }

    fun onDontKnowLanguageClicked(view: View) {
        if(filterModelLiveData.value!!.languageKnowledge == null) {
            filterModelLiveData.value!!.languageKnowledge = ArrayList()
        }
        if (filterModelLiveData.value!!.languageKnowledge!!.contains(LanguageKnowledge.DONT_KNOW)) {
            filterModelLiveData.value!!.languageKnowledge!!.remove(LanguageKnowledge.DONT_KNOW)
        } else {
            filterModelLiveData.value!!.languageKnowledge!!.add(LanguageKnowledge.DONT_KNOW)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isDontKnowLanguageChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.languageKnowledge == null) {
            false
        } else {
            filterModelLiveData.value!!.languageKnowledge!!.contains(LanguageKnowledge.DONT_KNOW)
        }
    }

    fun onKnowSomeWordsClicked(view: View) {
        if(filterModelLiveData.value!!.languageKnowledge == null) {
            filterModelLiveData.value!!.languageKnowledge = ArrayList()
        }
        if (filterModelLiveData.value!!.languageKnowledge!!.contains(LanguageKnowledge.KNOW_SOME_WORDS)) {
            filterModelLiveData.value!!.languageKnowledge!!.remove(LanguageKnowledge.KNOW_SOME_WORDS)
        } else {
            filterModelLiveData.value!!.languageKnowledge!!.add(LanguageKnowledge.KNOW_SOME_WORDS)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isKnowSomeWordsChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.languageKnowledge == null) {
            false
        } else {
            filterModelLiveData.value!!.languageKnowledge!!.contains(LanguageKnowledge.KNOW_SOME_WORDS)
        }
    }

    fun onUnderstandCantSpeakClicked(view: View) {
        if(filterModelLiveData.value!!.languageKnowledge == null) {
            filterModelLiveData.value!!.languageKnowledge = ArrayList()
        }
        if (filterModelLiveData.value!!.languageKnowledge!!.contains(LanguageKnowledge.UNDERSTAND_CANT_SPEAK)) {
            filterModelLiveData.value!!.languageKnowledge!!.remove(LanguageKnowledge.UNDERSTAND_CANT_SPEAK)
        } else {
            filterModelLiveData.value!!.languageKnowledge!!.add(LanguageKnowledge.UNDERSTAND_CANT_SPEAK)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isUnderstandCantSpeakChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.languageKnowledge == null) {
            false
        } else {
            filterModelLiveData.value!!.languageKnowledge!!.contains(LanguageKnowledge.UNDERSTAND_CANT_SPEAK)
        }
    }

    fun onUnderstandCanSpeakClicked(view: View) {
        if(filterModelLiveData.value!!.languageKnowledge == null) {
            filterModelLiveData.value!!.languageKnowledge = ArrayList()
        }
        if (filterModelLiveData.value!!.languageKnowledge!!.contains(LanguageKnowledge.UNDERSTAND_CAN_SPEAK)) {
            filterModelLiveData.value!!.languageKnowledge!!.remove(LanguageKnowledge.UNDERSTAND_CAN_SPEAK)
        } else {
            filterModelLiveData.value!!.languageKnowledge!!.add(LanguageKnowledge.UNDERSTAND_CAN_SPEAK)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isUnderstandCanSpeakChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.languageKnowledge == null) {
            false
        } else {
            filterModelLiveData.value!!.languageKnowledge!!.contains(LanguageKnowledge.UNDERSTAND_CAN_SPEAK)
        }
    }

    fun onKnowWellClicked(view: View) {
        if(filterModelLiveData.value!!.languageKnowledge == null) {
            filterModelLiveData.value!!.languageKnowledge = ArrayList()
        }
        if (filterModelLiveData.value!!.languageKnowledge!!.contains(LanguageKnowledge.KNOW_WELL)) {
            filterModelLiveData.value!!.languageKnowledge!!.remove(LanguageKnowledge.KNOW_WELL)
        } else {
            filterModelLiveData.value!!.languageKnowledge!!.add(LanguageKnowledge.KNOW_WELL)
        }

        filterModelLiveData.value = filterModelLiveData.value
    }

    fun isKnowWellChosen(): Boolean {
        return if(filterModelLiveData.value == null || filterModelLiveData.value?.languageKnowledge == null) {
            false
        } else {
            filterModelLiveData.value!!.languageKnowledge!!.contains(LanguageKnowledge.KNOW_WELL)
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
        if (this::locationApiResponse.isInitialized) {
            locationApiResponse.removeObserver(locationObserveResponse)
        }
        if (this::interestsApiResponse.isInitialized) {
            interestsApiResponse.removeObserver(interestsObserveResponse)
        }
        if (this::saveFilterApiResponse.isInitialized) {
            saveFilterApiResponse.removeObserver(saveFilterObserveResponse)
        }
        if (this::getCommercialApiResponse.isInitialized) {
            getCommercialApiResponse.removeObserver(getCommercialObserveResponse)
        }
    }

    fun getFilterModelLiveData(): LiveData<FilterModel> {
        return filterModelLiveData
    }

    fun getUsersListLiveData(): LiveData<MutableList<UserModel>> {
        return usersListLiveData
    }

    fun setShowFiltersLiveData(show: Boolean) {
        showFiltersLiveData.value = show
    }

    fun getShowFiltersLiveData(): LiveData<Boolean> {
        return showFiltersLiveData
    }

    fun getInterestsList(): LiveData<MutableList<InterestModel>> {
        return interestsList
    }

    fun getSelectedDistancePosition(): LiveData<Int> {
        return selectedDistancePosition
    }

    fun getShouldHitPagination(): Boolean {
        return shouldHitPagination
    }

    fun setShowGrowthWeightBottomSheet(show: Boolean) {
        showGrowthWeightBottomSheet.value = show
    }

    fun getShowGrowthWeightBottomSheet(): LiveData<Boolean> {
        return showGrowthWeightBottomSheet
    }
}