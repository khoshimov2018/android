package ru.behetem.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.behetem.R
import ru.behetem.interfaces.INationalityClick
import ru.behetem.models.CulturalInfoModel
import ru.behetem.models.FamilyInfoModel
import ru.behetem.models.NationalityModel
import ru.behetem.models.UserModel
import ru.behetem.repositories.NationalitiesRepository
import ru.behetem.repositories.UserRepository
import ru.behetem.responses.BaseResponse
import ru.behetem.utils.*

class NationalitiesViewModel(application: Application): BaseAndroidViewModel(application),
    INationalityClick {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private lateinit var apiResponse: LiveData<BaseResponse>
    private lateinit var observeResponse: Observer<BaseResponse>
    private val nationalitiesList: MutableLiveData<MutableList<NationalityModel>> = MutableLiveData()
    private val errorResId: MutableLiveData<Int> = MutableLiveData()
    private val errorTraditionResId: MutableLiveData<Int> = MutableLiveData()
    private val errorLanguageKnowledgeResId: MutableLiveData<Int> = MutableLiveData()
    private val errorReligionResId: MutableLiveData<Int> = MutableLiveData()

    private lateinit var submitApiResponse: LiveData<BaseResponse>
    private lateinit var submitObserveResponse: Observer<BaseResponse>

    fun getNationalities() {
        if (isInternetAvailable(context)) {
            showNoInternet.value = false
            loaderVisible.value = true // show loader

            observeResponse = Observer<BaseResponse> {
                loaderVisible.value = false

                if (validateResponseWithoutPopup(it)) {
                    if (it.data is MutableList<*>) {
                        val gson = Gson()
                        val strResponse = gson.toJson(it.data)
                        val myType = object : TypeToken<MutableList<NationalityModel>>() {}.type
                        val nationalities: MutableList<NationalityModel> = gson.fromJson<MutableList<NationalityModel>>(strResponse, myType)

                        for(nationality in nationalities) {
                            nationality.isSelected = false
                        }

                        nationalitiesList.value = nationalities
                    }
                } else {
                    baseResponse.value = it
                }
            }

            val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"
            apiResponse = NationalitiesRepository.getNationalities(strToken)
            apiResponse.observeForever(observeResponse)
        } else {
            showNoInternet.value = true
        }
    }

    override fun nationalityItemClicked(view: View, nationalityItem: NationalityModel) {
        nationalitiesList.value?.let {
            for(nationality in it) {
                nationality.isSelected = false
            }
        }
        nationalityItem.isSelected = true
        nationalitiesList.value = nationalitiesList.value
        errorResId.value = null
    }

    override fun moveFurther(view: View) {
        if(userModelLiveData.value?.culturalInfo == null) {
            userModelLiveData.value?.culturalInfo = CulturalInfoModel()
        }
        nationalitiesList.value?.let {
            for(nationality in it) {
                if(nationality.isSelected != null && nationality.isSelected!!) {
                    userModelLiveData.value?.culturalInfo?.nationality = nationality.nationalityId
                    break
                }
            }
        }
        when {
            userModelLiveData.value?.culturalInfo?.nationality == null -> {
                errorResId.value = R.string.choose_nationality
            }
            userModelLiveData.value?.culturalInfo?.traditionsRespect == null -> {
                errorTraditionResId.value = R.string.choose_tradition_respect
            }
            userModelLiveData.value?.culturalInfo?.languageKnowledge == null -> {
                errorLanguageKnowledgeResId.value = R.string.choose_language_proficiency
            }
            userModelLiveData.value?.culturalInfo?.religionRespect == null -> {
                errorReligionResId.value = R.string.choose_religion_respect
            }
            else -> {
                if(validateInternet(view.context)) {
//                        userModelLiveData.value?.selectedDOB = null
                    hideKeyboard(view)
                    loaderVisible.value = true // show loader
                    observeResponse = Observer<BaseResponse> { response ->
                        loaderVisible.value = false
                        if (validateResponse(view.context, response)) {
                            SharedPreferenceHelper.saveBooleanToShared(
                                view.context,
                                Constants.IS_USER_LOGGED_IN,
                                true
                            )
                            moveFurther.value = true
                        }
                    }
                    // token
                    val strToken = "${getLoggedInUser()?.tokenType} ${getLoggedInUser()?.jwt}"

                    apiResponse = UserRepository.changeInfo(userModelLiveData.value!!, strToken)
                    apiResponse.observeForever(observeResponse)
                }
            }
        }
    }

    fun onIDontKnowClicked(view: View) {
        if(userModelLiveData.value?.culturalInfo == null) {
            userModelLiveData.value?.culturalInfo = CulturalInfoModel()
        }
        userModelLiveData.value?.culturalInfo?.traditionsRespect = TraditionsRespect.DONT_KNOW
        errorTraditionResId.value = null
    }

    fun onIKnowButIDontClicked(view: View) {
        if(userModelLiveData.value?.culturalInfo == null) {
            userModelLiveData.value?.culturalInfo = CulturalInfoModel()
        }
        userModelLiveData.value?.culturalInfo?.traditionsRespect = TraditionsRespect.KNOW_NOT_RESPECT
        errorTraditionResId.value = null
    }

    fun onIKnowRespectClicked(view: View) {
        if(userModelLiveData.value?.culturalInfo == null) {
            userModelLiveData.value?.culturalInfo = CulturalInfoModel()
        }
        userModelLiveData.value?.culturalInfo?.traditionsRespect = TraditionsRespect.KNOW_RESPECT
        errorTraditionResId.value = null
    }

    fun onIDoNotKnowClicked(view: View) {
        if(userModelLiveData.value?.culturalInfo == null) {
            userModelLiveData.value?.culturalInfo = CulturalInfoModel()
        }
        userModelLiveData.value?.culturalInfo?.languageKnowledge = LanguageKnowledge.DONT_KNOW
        errorLanguageKnowledgeResId.value = null
    }

    fun onIKnowSomeWords(view: View) {
        if(userModelLiveData.value?.culturalInfo == null) {
            userModelLiveData.value?.culturalInfo = CulturalInfoModel()
        }
        userModelLiveData.value?.culturalInfo?.languageKnowledge = LanguageKnowledge.KNOW_SOME_WORDS
        errorLanguageKnowledgeResId.value = null
    }

    fun onIUnderstandButCannotMaintainDialogue(view: View) {
        if(userModelLiveData.value?.culturalInfo == null) {
            userModelLiveData.value?.culturalInfo = CulturalInfoModel()
        }
        userModelLiveData.value?.culturalInfo?.languageKnowledge = LanguageKnowledge.UNDERSTAND_CANT_SPEAK
        errorLanguageKnowledgeResId.value = null
    }

    fun onIUnderstandICanMaintainDialogue(view: View) {
        if(userModelLiveData.value?.culturalInfo == null) {
            userModelLiveData.value?.culturalInfo = CulturalInfoModel()
        }
        userModelLiveData.value?.culturalInfo?.languageKnowledge = LanguageKnowledge.UNDERSTAND_CAN_SPEAK
        errorLanguageKnowledgeResId.value = null
    }

    fun onIKnowWell(view: View) {
        if(userModelLiveData.value?.culturalInfo == null) {
            userModelLiveData.value?.culturalInfo = CulturalInfoModel()
        }
        userModelLiveData.value?.culturalInfo?.languageKnowledge = LanguageKnowledge.KNOW_WELL
        errorLanguageKnowledgeResId.value = null
    }

    fun onNonBelieverClicked(view: View) {
        if(userModelLiveData.value?.culturalInfo == null) {
            userModelLiveData.value?.culturalInfo = CulturalInfoModel()
        }
        userModelLiveData.value?.culturalInfo?.religionRespect = ReligionRespect.ATHEIST
        errorReligionResId.value = null
    }

    fun onBelieverClicked(view: View) {
        if(userModelLiveData.value?.culturalInfo == null) {
            userModelLiveData.value?.culturalInfo = CulturalInfoModel()
        }
        userModelLiveData.value?.culturalInfo?.religionRespect = ReligionRespect.RELIGIOUS
        errorReligionResId.value = null
    }

    fun onCanonicalBelieverClicked(view: View) {
        if(userModelLiveData.value?.culturalInfo == null) {
            userModelLiveData.value?.culturalInfo = CulturalInfoModel()
        }
        userModelLiveData.value?.culturalInfo?.religionRespect = ReligionRespect.CANONICAL_RELIGIOUS
        errorReligionResId.value = null
    }

    fun onSkipClicked(view: View) {
        moveFurther.value = true
    }

    override fun onCleared() {
        super.onCleared()
        if (this::apiResponse.isInitialized) {
            apiResponse.removeObserver(observeResponse)
        }
        if (this::submitApiResponse.isInitialized) {
            submitApiResponse.removeObserver(submitObserveResponse)
        }
    }

    fun getChosenGender(): String {
        return if(this.userModelLiveData.value != null && this.userModelLiveData.value!!.gender != null) {
            this.userModelLiveData.value!!.gender!!
        } else {
            ""
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

    fun getNationalitiesList(): LiveData<MutableList<NationalityModel>> {
        return nationalitiesList
    }

    fun getErrorResId(): LiveData<Int> {
        return errorResId
    }

    fun getErrorTraditionResId(): LiveData<Int> {
        return errorTraditionResId
    }

    fun getErrorLanguageKnowledgeResId(): LiveData<Int> {
        return errorLanguageKnowledgeResId
    }

    fun getErrorReligionResId(): LiveData<Int> {
        return errorReligionResId
    }
}