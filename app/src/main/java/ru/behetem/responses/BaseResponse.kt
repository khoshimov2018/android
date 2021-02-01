package ru.behetem.responses

data class BaseResponse(
    var code: Int? = null,
    var data: Any? = null
)