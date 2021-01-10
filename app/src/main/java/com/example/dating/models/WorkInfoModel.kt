package com.example.dating.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WorkInfoModel(
    var companyName: String? = null,
    var position: String? = null
): Parcelable