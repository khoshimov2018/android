package ru.behetem.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommercialModel(
    var status: String? = null,
    var statusExpirationDate: String? = null,
    var actionsLeft: ActionsLeftModel? = null
): Parcelable

@Parcelize
data class ActionsLeftModel(
    var STANDARD_REACTION: Int? = null,
    var ACTIVITY_CHECK: Int? = null,
    var SUPER_REACTION: Int? = null
): Parcelable
