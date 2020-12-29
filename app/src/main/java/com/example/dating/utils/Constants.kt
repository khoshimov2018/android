package com.example.dating.utils

object Constants {
    const val SPLASH_TIME_OUT = 3000L
    const val SHARED_PREF_NAME = "com.example.dating.shared"
    const val IS_USER_LOGGED_IN = "IS_USER_LOGGED_IN"
    const val LOGGED_IN_USER = "LOGGED_IN_USER"

    const val ERROR = "Error"
    const val SOMETHING_WENT_WRONG = "Something went wrong. Please try again later."
    const val COULD_NOT_CONNECT_TO_SERVER = "Could not connect to server, please try again later."

    const val LOG_TAG = "DATING_TAG"
}

object ApiConstants {
    const val BASE_URL = "http://37.143.14.155:8080/"
    const val LOGIN = "auth/signin"
}

object LoginFormErrorConstants {
    const val USERNAME_EMPTY = 1
    const val USERNAME_NOT_VALID = 2
    const val PASSWORD_EMPTY = 3
}