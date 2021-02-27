package ru.behetem.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FamilyInfoModel(
    var childrenDesire: Boolean? = null,
    var childrenPresence: String? = null,
    var status: String? = null
): Parcelable