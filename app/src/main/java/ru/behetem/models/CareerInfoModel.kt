package ru.behetem.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CareerInfoModel (
    var companyName: String? = null,
    var workPosition: String? = null,
    var graduationYear: Int? = null,
    var institutionName: String? = null,
    var educationLevel: String? = null
): Parcelable