package ru.behetem.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ru.behetem.R
import ru.behetem.models.FamilyInfoModel
import ru.behetem.models.UserModel
import ru.behetem.utils.ChildrenPresence
import ru.behetem.utils.FamilyStatus

class FamilyStatusViewModel : BaseViewModel() {

    private val userModelLiveData = MutableLiveData<UserModel>()
    private lateinit var apiResponse: LiveData<UserModel>
    private lateinit var observeResponse: Observer<UserModel>
    private val errorResId: MutableLiveData<Int> = MutableLiveData()
    private val errorChildrenPresenceResId: MutableLiveData<Int> = MutableLiveData()
    private val errorChildrenDesireResId: MutableLiveData<Int> = MutableLiveData()

    override fun moveFurther(view: View) {
        if (userModelLiveData.value == null || userModelLiveData.value?.familyInfo == null) {
            errorResId.value = R.string.choose_family_status
        } else if (userModelLiveData.value?.familyInfo?.status == null) {
            errorResId.value = R.string.choose_family_status
        } else if (userModelLiveData.value?.familyInfo?.childrenPresence == null) {
            errorChildrenPresenceResId.value = R.string.choose_children_presence
        } else if (userModelLiveData.value?.familyInfo?.childrenDesire == null) {
            errorChildrenDesireResId.value = R.string.choose_children_desire
        } else {
            moveFurther.value = true
        }
    }

    fun onSingleClicked(view: View) {
        if (userModelLiveData.value?.familyInfo == null) {
            userModelLiveData.value?.familyInfo = FamilyInfoModel()
        }
        userModelLiveData.value?.familyInfo?.status = FamilyStatus.SINGLE
        errorResId.value = null
    }

    fun onDivorcedClicked(view: View) {
        if (userModelLiveData.value?.familyInfo == null) {
            userModelLiveData.value?.familyInfo = FamilyInfoModel()
        }
        userModelLiveData.value?.familyInfo?.status = FamilyStatus.DIVORCED
        errorResId.value = null
    }

    fun onWidowedClicked(view: View) {
        if (userModelLiveData.value?.familyInfo == null) {
            userModelLiveData.value?.familyInfo = FamilyInfoModel()
        }
        userModelLiveData.value?.familyInfo?.status = FamilyStatus.WIDOWED
        errorResId.value = null
    }

    fun onNoChildrenClicked(view: View) {
        if (userModelLiveData.value?.familyInfo == null) {
            userModelLiveData.value?.familyInfo = FamilyInfoModel()
        }
        userModelLiveData.value?.familyInfo?.childrenPresence = ChildrenPresence.NONE
        errorChildrenPresenceResId.value = null
    }

    fun onLiveTogetherClicked(view: View) {
        if (userModelLiveData.value?.familyInfo == null) {
            userModelLiveData.value?.familyInfo = FamilyInfoModel()
        }
        userModelLiveData.value?.familyInfo?.childrenPresence = ChildrenPresence.TOGETHER
        errorChildrenPresenceResId.value = null
    }

    fun onLiveSeparatelyClicked(view: View) {
        if (userModelLiveData.value?.familyInfo == null) {
            userModelLiveData.value?.familyInfo = FamilyInfoModel()
        }
        userModelLiveData.value?.familyInfo?.childrenPresence = ChildrenPresence.APART
        errorChildrenPresenceResId.value = null
    }

    fun onYesDesireClicked(view: View) {
        if (userModelLiveData.value?.familyInfo == null) {
            userModelLiveData.value?.familyInfo = FamilyInfoModel()
        }
        userModelLiveData.value?.familyInfo?.childrenDesire = true
        errorChildrenDesireResId.value = null
    }

    fun onNoDesireClicked(view: View) {
        if (userModelLiveData.value?.familyInfo == null) {
            userModelLiveData.value?.familyInfo = FamilyInfoModel()
        }
        userModelLiveData.value?.familyInfo?.childrenDesire = false
        errorChildrenDesireResId.value = null
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

    fun getErrorResId(): LiveData<Int> {
        return errorResId
    }

    fun getErrorChildrenPresenceResId(): LiveData<Int> {
        return errorChildrenPresenceResId
    }

    fun getErrorChildrenDesireResId(): LiveData<Int> {
        return errorChildrenDesireResId
    }
}