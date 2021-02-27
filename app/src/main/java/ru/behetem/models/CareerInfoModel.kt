package ru.behetem.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.behetem.utils.EducationFormErrorConstants
import ru.behetem.utils.WorkInfoFormErrorConstants

@Parcelize
data class CareerInfoModel (
    var companyName: String? = null,
    var workPosition: String? = null,
    var graduationYear: Int? = null,
    var institutionName: String? = null,
    var educationLevel: String? = null
): Parcelable {
    fun isEducationValid(): Int {
        return when {
            isInstitutionNameEmpty() -> EducationFormErrorConstants.INSTITUTE_NAME_EMPTY
            isLevelEmpty() -> EducationFormErrorConstants.LEVEL_EMPTY
            isGraduationYearEmpty() -> EducationFormErrorConstants.YEAR_EMPTY
            isGraduationYearInvalid() -> EducationFormErrorConstants.YEAR_INVALID
            isPositionEmpty() -> EducationFormErrorConstants.POSITION_EMPTY
            isCompanyNameEmpty() -> EducationFormErrorConstants.COMPANY_NAME_EMPTY
            else -> 0
        }
    }

    private fun isInstitutionNameEmpty(): Boolean {
        return institutionName.isNullOrEmpty()
    }

    private fun isLevelEmpty(): Boolean {
        return educationLevel.isNullOrEmpty()
    }

    private fun isGraduationYearEmpty(): Boolean {
        return (graduationYear == null || graduationYear == 0)
    }

    private fun isGraduationYearInvalid(): Boolean {
        return (graduationYear!! < 1900 || graduationYear!! > 2050)
    }

    private fun isCompanyNameEmpty(): Boolean {
        return companyName.isNullOrEmpty()
    }

    private fun isPositionEmpty(): Boolean {
        return workPosition.isNullOrEmpty()
    }
}