package com.example.dating.utils

object Constants {
    const val SPLASH_TIME_OUT = 3000L
    const val SHARED_PREF_NAME = "com.example.dating.shared"
    const val IS_USER_LOGGED_IN = "IS_USER_LOGGED_IN"
    const val IS_REGISTRATION_DONE = "IS_REGISTRATION_DONE"
    const val LOGGED_IN_USER = "LOGGED_IN_USER"
    const val PROFILE_USER = "PROFILE_USER"
    const val DOB_DATE_FORMAT = "yyyy-MM-dd"
    const val DOB_ONLY_DATE_FORMAT = "dd"
    const val DOB_ONLY_MONTH_FORMAT = "MMM"
    const val DOB_ONLY_YEAR_FORMAT = "yyyy"
    const val MINIMUM_AGE = 18
    const val MINIMUM_PHOTOS = 3

    const val ERROR = "Error"
    const val SOMETHING_WENT_WRONG = "Something went wrong. Please try again later."
    const val COULD_NOT_CONNECT_TO_SERVER = "Could not connect to server, please try again later."

    const val PRIVACY_URL = "https://www.google.com"
    const val ABOUT_US_URL = "https://www.google.com"
    const val HELP_URL = "https://www.google.com"
    const val FEEDBACk_URL = "https://www.google.com"

    const val LOG_TAG = "DATING_TAG"
}

object ApiConstants {
    const val BASE_URL = "http://37.143.14.155:8080/"
    const val LOGIN = "auth/signin"
    const val REGISTRATION = "registration"
    const val CHANGE_INFO = "info"
    const val GET_INFO = "zinfo"
    const val UPLOAD_IMAGE = "upload-image"
    const val GET_INTERESTS = "common/interests"
    const val GET_NATIONALITIES = "common/nationalities"
}

object LoginFormErrorConstants {
    const val USERNAME_EMPTY = 1
    const val USERNAME_NOT_VALID = 2
    const val PASSWORD_EMPTY = 3
}

object BottomTabs {
    const val MY_PROFILE_TAB = 1
    const val PROFILES_TAB = 2
    const val MESSENGER_TAB = 3
}

object Gender {
    const val MALE = "MALE"
    const val FEMALE = "FEMALE"
}

object DobErrorConstants {
    const val DOB_EMPTY = 1
    const val AGE_LESS = 2
}