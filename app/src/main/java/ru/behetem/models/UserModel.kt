package ru.behetem.models

import android.os.Parcelable
import android.util.Patterns
import ru.behetem.utils.*
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
    var roles: MutableList<String>? = null,
    var age: Int? = null,
    var interests: MutableList<String>? = null,
    var images: MutableList<String> ? = null,
    var distanceTo: String? = null,
    var reactions: MutableList<ReactionModel>? = null,
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
//            isDobEmpty() -> EditProfileErrorConstants.DOB_EMPTY
            isAgeLessEditProfile() -> EditProfileErrorConstants.AGE_LESS
//            isInterestEmpty() -> EditProfileErrorConstants.INTEREST_EMPTY
//            isDescriptionEmpty() -> EditProfileErrorConstants.ABOUT_ME_EMPTY
//            isGrowthEmpty() -> EditProfileErrorConstants.GROWTH_EMPTY
//            isWeightEmpty() -> EditProfileErrorConstants.WEIGHT_EMPTY
//            isNationalityEmpty() -> EditProfileErrorConstants.NATIONALITY_EMPTY
//            isPositionEmpty() -> EditProfileErrorConstants.POSITION_EMPTY
//            isCompanyNameEmpty() -> EditProfileErrorConstants.COMPANY_NAME_EMPTY
//            isInstitutionNameEmpty() -> EditProfileErrorConstants.INSTITUTE_NAME_EMPTY
//            isLevelEmpty() -> EditProfileErrorConstants.LEVEL_EMPTY
//            isGraduationYearEmpty() -> EditProfileErrorConstants.YEAR_EMPTY
//            isGraduationYearInvalid() -> EditProfileErrorConstants.YEAR_INVALID
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

    fun getAge(): String {
        if (dateOfBirth.isNullOrEmpty()) {
            return ""
        } else {
            val current = Calendar.getInstance()
            val dob = getCalendarFromDob(dateOfBirth!!) ?: return ""
            return "${getDifferenceInYears(dob, current)}"
        }
    }

    fun getNameAndAge(): String {
        var str = ""
        if(name != null) {
            str = "$name"
        }
        if(age != null) {
            str += ", ${age.toString()}"
        }
        return str
    }

    fun getShortDescription(): String {
        return description?.let { trimText(it, Constants.SHORT_DESCRIPTION_TRIM_LENGTH) } ?: ""
    }

    fun getPosition(): String {
        return if (workInfo == null || workInfo!!.position == null) ""
        else workInfo!!.position!!
    }

    fun getCompanyName(): String {
        return if (workInfo == null || workInfo!!.companyName == null) ""
        else workInfo!!.companyName!!
    }

    fun getWorkInfo(): String {
        return if (workInfo == null || workInfo!!.position == null) ""
        else "${workInfo?.position}, ${workInfo?.companyName}"
    }

    fun getInstituteName(): String {
        return if (educationInfo == null || educationInfo!!.institutionName == null) ""
        else "${educationInfo?.institutionName}"
    }

    fun getGraduationYear(): String {
        return if (educationInfo == null || educationInfo!!.graduationYear == null) ""
        else "${educationInfo?.graduationYear}"
    }

    fun getLevel(): String {
        return "${educationInfo?.level}"
    }

    fun getEducationInfo(): String {
        return if (educationInfo == null || educationInfo!!.institutionName == null) ""
        else "${educationInfo?.institutionName}, ${educationInfo?.graduationYear}, ${educationInfo?.level}"
    }

    private fun isDobEmpty(): Boolean {
        return selectedDOB == null
    }

    private fun isAgeLess(): Boolean {
        val current = Calendar.getInstance()
        return getDifferenceInYears(selectedDOB!!, current) < Constants.MINIMUM_AGE
    }

    private fun isAgeLessEditProfile(): Boolean {
        return if (selectedDOB == null) {
            false
        } else {
            val current = Calendar.getInstance()
            getDifferenceInYears(selectedDOB!!, current) < Constants.MINIMUM_AGE
        }
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

    private fun isInterestEmpty(): Boolean {
        return interestLabels == null || interestLabels!!.size == 0
    }

    private fun isDescriptionEmpty(): Boolean {
        return description.isNullOrEmpty()
    }

    private fun isGrowthEmpty(): Boolean {
        return growth == null || growth == 0
    }

    private fun isWeightEmpty(): Boolean {
        return weight == null || weight == 0
    }

    private fun isNationalityEmpty(): Boolean {
        return nationality.isNullOrEmpty()
    }

    private fun isCompanyNameEmpty(): Boolean {
        return workInfo?.companyName.isNullOrEmpty()
    }

    private fun isPositionEmpty(): Boolean {
        return workInfo?.position.isNullOrEmpty()
    }

    private fun isInstitutionNameEmpty(): Boolean {
        return educationInfo?.institutionName.isNullOrEmpty()
    }

    private fun isLevelEmpty(): Boolean {
        return educationInfo?.level.isNullOrEmpty()
    }

    private fun isGraduationYearEmpty(): Boolean {
        return (educationInfo?.graduationYear == null || educationInfo?.graduationYear == 0)
    }

    private fun isGraduationYearInvalid(): Boolean {
        return (educationInfo?.graduationYear!! < 1900 || educationInfo?.graduationYear!! > 2050)
    }
}