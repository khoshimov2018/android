package ru.behetem.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.behetem.utils.WorkInfoFormErrorConstants

@Parcelize
data class WorkInfoModel(
    var companyName: String? = null,
    var position: String? = null
): Parcelable {
    fun isWorkInfoValid(): Int {
        return when {
            isPositionEmpty() -> WorkInfoFormErrorConstants.POSITION_EMPTY
            isCompanyNameEmpty() -> WorkInfoFormErrorConstants.COMPANY_NAME_EMPTY
            else -> 0
        }
    }

    private fun isCompanyNameEmpty(): Boolean {
        return companyName.isNullOrEmpty()
    }

    private fun isPositionEmpty(): Boolean {
        return position.isNullOrEmpty()
    }
}