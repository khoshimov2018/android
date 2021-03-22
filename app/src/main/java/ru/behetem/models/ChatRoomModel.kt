package ru.behetem.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatRoomModel(
    var id: Int? = null,
    var chatId: String? = null,
    var senderId: String? = null,
    var recipientId: String? = null,
    var receiverName: String? = null,
    var receiverImage: String? = null,
    var lastMessage: MessageModel? = null
): Parcelable