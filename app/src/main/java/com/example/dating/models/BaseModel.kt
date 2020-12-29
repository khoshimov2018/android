package com.example.dating.models

abstract class BaseModel {
    var jwt: String? = null
    var tokenType: String? = null
    var status: Int? = null
    var error: String? = null
    var message: String? = null
}