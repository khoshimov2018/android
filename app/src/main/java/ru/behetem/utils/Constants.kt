package ru.behetem.utils

object Constants {
    const val SPLASH_TIME_OUT = 3000L
    const val SHARED_PREF_NAME = "com.example.dating.shared"
    const val IS_USER_LOGGED_IN = "IS_USER_LOGGED_IN"
    const val IS_REGISTRATION_DONE = "IS_REGISTRATION_DONE"
    const val LOGGED_IN_USER = "LOGGED_IN_USER"
    const val PROFILE_USER = "PROFILE_USER"
    const val USER_IMAGES = "USER_IMAGES"
    const val USER_FILTERS = "USER_FILTERS"
    const val RECEIVED_REACTION = "RECEIVED_REACTION"
    const val CHAT_ROOM = "CHAT_ROOM"
    const val DOB_DATE_FORMAT = "yyyy-MM-dd"
    const val DOB_ONLY_DATE_FORMAT = "dd"
    const val DOB_ONLY_MONTH_FORMAT = "MMM"
    const val DOB_ONLY_YEAR_FORMAT = "yyyy"
    const val MINIMUM_AGE = 18
    const val MINIMUM_PHOTOS = 3
    const val TRIM_TEXT_LENGTH = 15
    const val SHORT_DESCRIPTION_TRIM_LENGTH = 40
    const val PAGE_SIZE = 10
    const val MIN_AGE_FOR_CAL = 18
    const val MAX_GROWTH_WEIGHT = 250

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
    const val BASE_URL = "http://behetem.ru/"
    const val WEB_SOCKET_URL = "ws://37.143.14.155/ws/websocket"
    const val LOGIN = "auth/signin"
    const val REGISTRATION = "registration"
    const val CHANGE_INFO = "info"
    const val GET_INFO = "info"
    const val UPLOAD_IMAGE = "images/upload"
    const val GET_INTERESTS = "common/interests"
    const val GET_NATIONALITIES = "common/nationalities"
    const val GET_CURRENT_USER_IMAGES = "images?version=original"
    const val SAVE_FILTERS = "filters"
    const val GET_FILTERS = "filters"
    const val GET_USERS = "users"
    const val UPDATE_LOCATION = "location/update"
    const val DELETE_IMAGE = "images/delete"
    const val SEND_REACTION = "react"
    const val GET_REACTIONS = "reaction/newReactions"
    const val CHANGE_PASSWORD = "password/change"
    const val DELETE_ACCOUNT = "delete-account"
    const val GET_USER_DETAIL = "user"
    const val CHANGE_REACTION = "reaction/change"
    const val GET_CHAT_ROOM = "messages/chatroom"
    const val DELETE_CHAT_ROOM = "messages/deletechatroom"
    const val GET_COMMERCIAL = "commercial"
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

object EducationFormErrorConstants {
    const val INSTITUTE_NAME_EMPTY = 1
    const val LEVEL_EMPTY = 2
    const val YEAR_EMPTY = 3
    const val YEAR_INVALID = 4
    const val COMPANY_NAME_EMPTY = 5
    const val POSITION_EMPTY = 6
}

object WorkInfoFormErrorConstants {
    const val COMPANY_NAME_EMPTY = 1
    const val POSITION_EMPTY = 2
}

object ImageVersions {
    const val ORIGINAL = "original"
    const val USUAL = "usual"
    const val MINI = "mini"
}

object EducationLevels {
    const val POWER = "Степень"
    const val GENERAL = "GENERAL"
    const val SECONDARY = "SECONDARY"
    const val SPECIALIZED_SECONDARY = "SPECIALIZED_SECONDARY"
    const val INCOMPLETE_HIGHER = "INCOMPLETE_HIGHER"
    const val HIGHER = "HIGHER"
}

object EditProfileErrorConstants {
    const val NAME_EMPTY = 1
    const val DOB_EMPTY = 2
    const val AGE_LESS = 3
    const val INTEREST_EMPTY = 4
    const val ABOUT_ME_EMPTY = 5
    const val GROWTH_EMPTY = 6
    const val WEIGHT_EMPTY = 7
    const val NATIONALITY_EMPTY = 8
    const val COMPANY_NAME_EMPTY = 9
    const val POSITION_EMPTY = 10
    const val INSTITUTE_NAME_EMPTY = 11
    const val LEVEL_EMPTY = 12
    const val YEAR_EMPTY = 13
    const val YEAR_INVALID = 14
}

object ChangePasswordFormErrorConstants {
    const val OLD_EMPTY = 1
    const val NEW_EMPTY = 2
    const val CONFIRM_EMPTY = 3
    const val DO_NOT_MATCH = 4
}

object FamilyStatus {
    const val SINGLE = "SINGLE"
    const val DIVORCED = "DIVORCED"
    const val WIDOWED = "WIDOWED"
}

object ChildrenPresence {
    const val NONE = "NONE"
    const val TOGETHER = "TOGETHER"
    const val APART = "APART"
}

object TraditionsRespect {
    const val DONT_KNOW = "DONT_KNOW"
    const val KNOW_NOT_RESPECT = "KNOW_NOT_RESPECT"
    const val KNOW_RESPECT = "KNOW_RESPECT"
}

object ReligionRespect {
    const val ATHEIST = "ATHEIST"
    const val RELIGIOUS = "RELIGIOUS"
    const val CANONICAL_RELIGIOUS = "CANONICAL_RELIGIOUS"
}

object LanguageKnowledge {
    const val DONT_KNOW = "DONT_KNOW"
    const val KNOW_SOME_WORDS = "KNOW_SOME_WORDS"
    const val UNDERSTAND_CANT_SPEAK = "UNDERSTAND_CANT_SPEAK"
    const val UNDERSTAND_CAN_SPEAK = "UNDERSTAND_CAN_SPEAK"
    const val KNOW_WELL = "KNOW_WELL"
}

object BodyType {
    const val THIN = "THIN"
    const val SLIM = "SLIM"
    const val ATHLETIC = "ATHLETIC"
    const val PLUMP = "PLUMP"
    const val FAT = "FAT"
}

object Languages {
    const val RU = "RU"
    const val EN = "EN"
    const val AR = "AR"
}

object ReactionStatuses {
    const val DELETED = "DELETED"
    const val EXPIRED = "EXPIRED"
    const val IGNORED = "IGNORED"
    const val MUTUAL = "MUTUAL"
    const val SEND = "SEND"
    const val SHOWED = "SHOWED"
}

object MessageTypes {
    const val TEXT = "TEXT"
    const val VOICE = "VOICE"
    const val IMAGE = "IMAGE"
    const val CHAT_DELETED_AND_BLOCKED = "CHAT_DELETED_AND_BLOCKED"
    const val CHAT_EXPIRED = "CHAT_EXPIRED"
}

object ReactionType {
    const val STANDARD = "STANDARD"
    const val SUPER = "SUPER"
}