package com.example.dating.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EducationInfoModel(
    var graduationYear: Int? = null,
    var institutionName: String? = null,
    var level: String? = null
): Parcelable