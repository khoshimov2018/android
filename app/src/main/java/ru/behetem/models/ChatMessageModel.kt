package ru.behetem.models

import ru.behetem.utils.getOnlyTimeFromUtcDateTime

data class ChatMessageModel(
    var id: Int? = null,
    var chatId: String? = null,
    var senderId: String? = null,
    var recipientId: String? = null,
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
}