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
    var gender: String? = null,
//    var interestLabels: MutableList<String>? = null,
    var name: String? = null,
    var selectedDOB: Calendar? = null,
    var roles: MutableList<String>? = null,
    var age: Int? = null,
    var interests: MutableList<String>? = null,
    var images: MutableList<String>? = null,
    var distanceTo: String? = null,
    var reactions: MutableList<ReactionModel>? = null,
    var nationalityToShow: String? = null,
    var bodyInfo: BodyInfoModel? = null,
    var careerInfo: CareerInfoModel? = null,
    var culturalInfo: CulturalInfoModel? = null,
    var familyInfo: FamilyInfoModel? = null,
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
        if (name != null) {
            str = "$name"
        }
        if (age != null) {
            str += ", ${age.toString()}"
        }
        return str
    }

    fun getShortDescription(): String {
        return description?.let { trimText(it, Constants.SHORT_DESCRIPTION_TRIM_LENGTH) } ?: ""
    }

    fun getPosition(): String {
        return if (careerInfo == null || careerInfo!!.workPosition == null) ""
        else careerInfo!!.workPosition!!
    }

    fun getCompanyName(): String {
        return if (careerInfo == null || careerInfo!!.companyName == null) ""
        else careerInfo!!.companyName!!
    }

    fun getWorkInfo(): String {
        return if (careerInfo == null || careerInfo!!.workPosition == null) ""
        else "${careerInfo?.workPosition}, ${careerInfo?.companyName}"
    }

    fun getInstituteName(): String {
        return if (careerInfo == null || careerInfo!!.institutionName == null) ""
        else "${careerInfo?.institutionName}"
    }

    fun getGraduationYear(): String {
        return if (careerInfo == null || careerInfo!!.graduationYear == null) ""
        else "${careerInfo?.graduationYear}"
    }

    fun getLevel(): String {
        return "${careerInfo?.educationLevel}"
    }

    fun getEducationInfo(): String {
        return if (careerInfo == null || careerInfo!!.institutionName == null) ""
        else "${careerInfo?.institutionName}, ${careerInfo?.graduationYear}, ${careerInfo?.educationLevel}"
    }

    fun getDisplayableNationality(): String {
        return if (nationalityToShow.isNullOrEmpty()) {
            if (culturalInfo == null && culturalInfo?.nationality.isNullOrEmpty()) {
                ""
            } else {
                culturalInfo?.nationality!!
            }
        } else {
            nationalityToShow!!
        }
    }

    fun isSingle(): Boolean {
        return familyInfo?.status == FamilyStatus.SINGLE
    }

    fun isDivorced(): Boolean {
        return familyInfo?.status == FamilyStatus.DIVORCED
    }

    fun isWidowed(): Boolean {
        return familyInfo?.status == FamilyStatus.WIDOWED
    }

    fun isNoChildren(): Boolean {
        return familyInfo?.childrenPresence == ChildrenPresence.NONE
    }

    fun isLiveTogether(): Boolean {
        return familyInfo?.childrenPresence == ChildrenPresence.TOGETHER
    }

    fun isLiveSeparately(): Boolean {
        return familyInfo?.childrenPresence == ChildrenPresence.APART
    }

    fun isYesDesire(): Boolean {
        return familyInfo?.childrenDesire?.let { it } ?: false
    }

    fun isDontRespect(): Boolean {
        return culturalInfo?.traditionsRespect == TraditionsRespect.DONT_KNOW
    }

    fun isKnowButDont(): Boolean {
        return culturalInfo?.traditionsRespect == TraditionsRespect.KNOW_NOT_RESPECT
    }

    fun isKnowRespect(): Boolean {
        return culturalInfo?.traditionsRespect == TraditionsRespect.KNOW_RESPECT
    }

    fun isSkinnySelected(): Boolean {
        return bodyInfo?.bodyType == BodyType.THIN
    }

    fun isSlenderSelected(): Boolean {
        return bodyInfo?.bodyType == BodyType.SLIM
    }

    fun isSportsSelected(): Boolean {
        return bodyInfo?.bodyType == BodyType.ATHLETIC
    }

    fun isDenseSelected(): Boolean {
        return bodyInfo?.bodyType == BodyType.PLUMP
    }

    fun isCompleteSelected(): Boolean {
        return bodyInfo?.bodyType == BodyType.FAT
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
        return interests == null || interests!!.size == 0
    }

    private fun isDescriptionEmpty(): Boolean {
        return description.isNullOrEmpty()
    }

    private fun isGrowthEmpty(): Boolean {
        return bodyInfo?.growth == null || bodyInfo?.growth == 0
    }

    private fun isWeightEmpty(): Boolean {
        return bodyInfo?.weight == null || bodyInfo?.weight == 0
    }

    private fun isNationalityEmpty(): Boolean {
        return culturalInfo?.nationality.isNullOrEmpty()
    }

    private fun isCompanyNameEmpty(): Boolean {
        return careerInfo?.companyName.isNullOrEmpty()
    }

    private fun isPositionEmpty(): Boolean {
        return careerInfo?.workPosition.isNullOrEmpty()
    }

    private fun isInstitutionNameEmpty(): Boolean {
        return careerInfo?.institutionName.isNullOrEmpty()
    }

    private fun isLevelEmpty(): Boolean {
        return careerInfo?.educationLevel.isNullOrEmpty()
    }

    private fun isGraduationYearEmpty(): Boolean {
        return (careerInfo?.graduationYear == null || careerInfo?.graduationYear == 0)
    }

    private fun isGraduationYearInvalid(): Boolean {
        return (careerInfo?.graduationYear!! < 1900 || careerInfo?.graduationYear!! > 2050)
    }
}