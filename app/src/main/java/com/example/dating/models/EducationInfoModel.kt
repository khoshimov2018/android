package com.example.dating.models

import android.os.Parcelable
import com.example.dating.utils.EducationFormErrorConstants
import com.example.dating.utils.LoginFormErrorConstants
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EducationInfoModel(
    var graduationYear: Int? = null,
    var institutionName: String? = null,
    var level: String? = null
): Parcelable {
    fun isEducationValid(): Int {
        return when {
            isInstitutionNameEmpty() -> EducationFormErrorConstants.INSTITUTE_NAME_EMPTY
            isLevelEmpty() -> EducationFormErrorConstants.LEVEL_EMPTY
            isGraduationYearEmpty() -> EducationFormErrorConstants.YEAR_EMPTY
            isGraduationYearInvalid() -> EducationFormErrorConstants.YEAR_INVALID
            else -> 0
        }
    }

    private fun isInstitutionNameEmpty(): Boolean {
        return institutionName.isNullOrEmpty()
    }

    private fun isLevelEmpty(): Boolean {
        return level.isNullOrEmpty()
    }

    private fun isGraduationYearEmpty(): Boolean {
        return (graduationYear == null || graduationYear == 0)
    }

    private fun isGraduationYearInvalid(): Boolean {
        return (graduationYear!! < 1900 || graduationYear!! > 2050)
    }
}