package ru.behetem.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageModel(
    var id: Int? = null,
    var chatId: String? = null,
    var senderId: String? = null,
    var recipientId: String? = null,
    var senderName: String? = null,
    var recipientName: String? = null,
    var messageType: String? = null,
    var content: String? = null,
    var timestamp: String? = null,
    var status: String? = null,
): Parcelable