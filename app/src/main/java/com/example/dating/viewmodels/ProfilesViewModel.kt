package com.example.dating.viewmodels

import android.app.Application
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.dating.R
import com.example.dating.interfaces.IInterestClick
import com.example.dating.models.*
import com.example.dating.repositories.*
import com.example.dating.responses.BaseResponse
import com.example.dating.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProfilesViewModel(application: Application) : BaseAndroidViewModel(application),
    IInterestClick {

    private var filterModelLiveData: MutableLiveData<FilterModel> = MutableLiveData()
    private val baseResponse: MutableLiveData<BaseResponse> = MutableLiveData()

    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>

    private lateinit var saveFilterApiResponse: LiveData<BaseResponse>
    private lateinit var saveFilterObserveResponse: Observer<BaseResponse>

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

    fun saveFilters() {
        if(validateInternet(context)) {
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

    private fun getUsers() {
        if (isInternetAvailable(context)) {
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

                        usersListLiveData.value = usersList
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
                    getFilters()
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
                        val myType = object : TypeToken<MutableList<String>>() {}.type
                        val interestStringList: MutableList<String> =
                            gson.fromJson<MutableList<String>>(strResponse, myType)

                        val tempInterestsList = java.util.ArrayList<InterestModel>()

                        for (interest in interestStringList) {
                            if (filterModelLiveData.value != null && filterModelLiveData.value!!.interest != null) {
                                var isSaved = false
                                for (savedInterest in filterModelLiveData.value!!.interest!!) {
                                    if (interest.equals(savedInterest, ignoreCase = true)) {
                                        isSaved = true
                                        break
                                    }
                                }
                                if (isSaved) {
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

    fun onFilterClick(view: View) {
        if (interestsList.value == null || interestsList.value!!.isEmpty()) {
            getInterests()
            showFiltersLiveData.value = true
        } else {
            showFiltersLiveData.value = true
        }
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

        filterModelLiveData.value?.let {
            saveFiltersToShared(context, it)
        }
        filterModelLiveData.value = filterModelLiveData.value
    }

    fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val array = view?.context?.resources?.getStringArray(R.array.distance_level)
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
                        filterModelLiveData.value?.interest?.add(interest.label!!)
                    }
                }
            }
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
    }

    fun getBaseResponse(): LiveData<BaseResponse?> {
        return baseResponse
    }

    fun setBaseResponse(baseResponse: BaseResponse?) {
        this.baseResponse.value = baseResponse
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
}