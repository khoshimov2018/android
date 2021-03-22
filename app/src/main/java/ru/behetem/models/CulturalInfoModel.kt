package ru.behetem.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CulturalInfoModel (
    var languageKnowledge: String? = null,
    var nationality: String? = null,
    var religionRespect: String? = null,
    var traditionsRespect: String? = null
): Parcelable