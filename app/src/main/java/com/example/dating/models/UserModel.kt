package com.example.dating.models

import android.os.Parcelable
import android.util.Patterns
import com.example.dating.utils.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class UserModel(
    var username: String? = null,
    var password: String? = null,
    var id: Int? = null,
    var email: String? = null,
    var dateOfBirth: String? = null,
    var description: String? = null,
    var educationInfo: EducationInfoModel? = null,
    var gender: String? = null,
    var growth: Int? = null,
    var interestLabels: MutableList<String>? = null,
    var name: String? = null,
    var nationality: String? = null,
    var weight: Int? = null,
    var workInfo: WorkInfoModel? = null,
    var selectedDOB: Calendar? = null,
    // roles - not parsing as only user will login to the app
) : Parcelable, BaseModel() {
    fun validateLoginData(): Int {
        return when {
            isUsernameEmpty() -> LoginFormErrorConstants.USERNAME_EMPTY
            !isUsernameValid() -> LoginFormErrorConstants.USERNAME_NOT_VALID
            isPasswordEmpty() -> LoginFormErrorConstants.PASSWORD_EMPTY
            else -> 0
        }
    }

    fun validateRegistrationData(): Int {
        return when {
            isEmailEmpty() -> LoginFormErrorConstants.USERNAME_EMPTY
            !isEmailValid() -> LoginFormErrorConstants.USERNAME_NOT_VALID
            isPasswordEmpty() -> LoginFormErrorConstants.PASSWORD_EMPTY
            else -> 0
        }
    }

    fun isMale(): Boolean {
        return gender == Gender.MALE
    }

    fun isFemale(): Boolean {
        return gender == Gender.FEMALE
    }

    fun isGenderSelected(): Boolean {
        return gender != null
    }

    fun isNameEntered(): Boolean {
        return !name.isNullOrEmpty()
    }

    fun getDate(): String {
        return selectedDOB?.let { getDisplayableDate(it) } ?: ""
    }

    fun getMonth(): String {
        return selectedDOB?.let { getDisplayableMonth(it) } ?: ""
    }

    fun getYear(): String {
        return selectedDOB?.let { getDisplayableYear(it) } ?: ""
    }

    fun validateDob(): Int {
        return when {
            isDobEmpty() -> DobErrorConstants.DOB_EMPTY
            isAgeLess() -> DobErrorConstants.AGE_LESS
            else -> 0
        }
    }

    private fun isDobEmpty(): Boolean {
        return selectedDOB == null
    }

    private fun isAgeLess(): Boolean {
        val current = Calendar.getInstance()
        return current.get(Calendar.YEAR) - selectedDOB!!.get(Calendar.YEAR) < Constants.MINIMUM_AGE
    }

    private fun isUsernameEmpty(): Boolean {
        return username.isNullOrEmpty()
    }

    private fun isUsernameValid(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username.toString()).matches()
    }

    private fun isEmailEmpty(): Boolean {
        return email.isNullOrEmpty()
    }

    private fun isEmailValid(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()
    }

    private fun isPasswordEmpty(): Boolean {
        return password.isNullOrEmpty()
    }
}