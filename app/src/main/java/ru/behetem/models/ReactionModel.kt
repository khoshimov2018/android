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
    var createdDate: String? = null,
    var interactionStatus: String? = null,
    var senderId: Int? = null,
    var image: String? = null
): Parcelable