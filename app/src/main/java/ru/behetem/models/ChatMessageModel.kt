package ru.behetem.models

import ru.behetem.utils.ApiConstants
import ru.behetem.utils.MessageTypes
import ru.behetem.utils.getOnlyTimeFromUtcDateTime

data class ChatMessageModel(
    var id: Int? = null,
    var chatId: String? = null,
    var senderId: Int? = null,
    var recipientId: Int? = null,
    var senderName: String? = null,
    var recipientName: String? = null,
    var messageType: String? = null,
    var content: String? = null,
    var timestamp: String? = null,
    var status: String? = null
) {
    fun getTime(): String {
        return if(timestamp == null) {
            ""
        } else {
            getOnlyTimeFromUtcDateTime(timestamp!!)
        }
    }

    fun isTextMessage(): Boolean {
        return messageType == null || messageType == MessageTypes.TEXT
    }

    fun isImageMessage(): Boolean {
        return messageType == MessageTypes.IMAGE
    }

    fun getImageUrl(): String {
        return content?.let { "${ApiConstants.BASE_URL}${it}" } ?: ""
    }
}