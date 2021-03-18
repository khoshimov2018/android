package ru.behetem.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.behetem.utils.ReactionType

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
    var image: String? = null,
    var senderName: String? = null,
    var reactionId: Int? = null,
    var status: String? = null
): Parcelable {
    fun isSuper(): Boolean {
        return type == ReactionType.SUPER
    }
}