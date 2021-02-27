package ru.behetem.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BodyInfoModel(
    var bodyType: String? = null,
    var growth: Int? = null,
    var weight: Int? = null,
): Parcelable