package ru.behetem.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReactionModel(
    var id: Int? = null,
    var emoji: String? = null,
    var type: String? = null,
    var mood: String? = null,
    var reaction: Int? = null,
    var receiverId: Int? = null,
): Parcelable