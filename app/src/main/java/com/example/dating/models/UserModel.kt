package com.example.dating.models

import android.os.Parcelable
import android.util.Patterns
import com.example.dating.utils.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class UserModel(
    var jwt: String? = null,
    var tokenType: String? = null,
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
    var lookingFor: String? = null,
    var roles: MutableList<String>? = null,
) : Parcelable {
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

    fun validateEditProfile(): Int {
        return when {
            isNameEmpty() -> EditProfileErrorConstants.NAME_EMPTY
            isDobEmpty() -> EditProfileErrorConstants.DOB_EMPTY
            isAgeLess() -> EditProfileErrorConstants.AGE_LESS
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

    fun isNameEmpty(): Boolean {
        return name.isNullOrEmpty()
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

    fun isLookingForMale(): Boolean {
        return lookingFor == Gender.MALE
    }

    fun isLookingForFemale(): Boolean {
        return lookingFor == Gender.FEMALE
    }

    fun isLookingForSelected(): Boolean {
        return lookingFor != null
    }

    fun getAge(): String {
        if(dateOfBirth.isNullOrEmpty()) {
            return ""
        } else {
            val current = Calendar.getInstance()
            val dob = getCalendarFromDob(dateOfBirth!!) ?: return ""
            return "${getDifferenceInYears(dob, current)}"
        }
    }

    fun getShortDescription(): String {
        return description?.let { trimText(it, Constants.SHORT_DESCRIPTION_TRIM_LENGTH) } ?: ""
    }

    fun getPosition(): String {
        return workInfo?.position!!
    }

    fun getCompanyName(): String {
        return workInfo?.companyName!!
    }

    fun getWorkInfo(): String {
        return "${workInfo?.position}, ${workInfo?.companyName}"
    }

    fun getInstituteName(): String {
        return "${educationInfo?.institutionName}"
    }

    fun getGraduationYear(): String {
        return "${educationInfo?.graduationYear}"
    }

    fun getLevel(): String {
        return "${educationInfo?.level}"
    }

    fun getEducationInfo(): String {
        return "${educationInfo?.institutionName}, ${educationInfo?.graduationYear}, ${educationInfo?.level}"
    }

    private fun isDobEmpty(): Boolean {
        return selectedDOB == null
    }

    private fun isAgeLess(): Boolean {
        val current = Calendar.getInstance()
        return getDifferenceInYears(selectedDOB!!, current) < Constants.MINIMUM_AGE
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