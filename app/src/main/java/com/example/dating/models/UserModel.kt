package com.example.dating.models

import android.os.Parcelable
import android.util.Patterns
import com.example.dating.utils.LoginFormErrorConstants
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    var username: String? = null,
    var password: String? = null,
    var id: Int? = null,
    var email: String? = null,
    // roles - not parsing as only user will login to the app
): Parcelable, BaseModel() {

    fun validateLoginData(): Int {
        return when {
            isUsernameEmpty() -> LoginFormErrorConstants.USERNAME_EMPTY
            !isUsernameValid() -> LoginFormErrorConstants.USERNAME_NOT_VALID
            isPasswordEmpty() -> LoginFormErrorConstants.PASSWORD_EMPTY
            else -> 0
        }
    }

    private fun isUsernameEmpty(): Boolean {
        return username.isNullOrEmpty()
    }

    private fun isUsernameValid(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username.toString()).matches()
    }

    private fun isPasswordEmpty(): Boolean {
        return password.isNullOrEmpty()
    }
}