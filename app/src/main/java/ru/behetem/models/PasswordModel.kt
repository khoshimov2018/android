package ru.behetem.models

import ru.behetem.utils.ChangePasswordFormErrorConstants
import ru.behetem.utils.LoginFormErrorConstants

data class ChangePasswordModel(
    var passwords: PasswordModel? = null
)

data class PasswordModel(
    var old: String? = null,
    var new: String? = null,
    var confirm: String? = null
) {
    fun validateChangePassword(): Int{
        return when {
            isOldEmpty() -> ChangePasswordFormErrorConstants.OLD_EMPTY
            isNewEmpty() -> ChangePasswordFormErrorConstants.NEW_EMPTY
            isConfirmEmpty() -> ChangePasswordFormErrorConstants.CONFIRM_EMPTY
            !doPasswordsMatch() -> ChangePasswordFormErrorConstants.DO_NOT_MATCH
            else -> 0
        }
    }

    private fun isOldEmpty(): Boolean {
        return old.isNullOrEmpty()
    }

    private fun isNewEmpty(): Boolean {
        return new.isNullOrEmpty()
    }

    private fun isConfirmEmpty(): Boolean {
        return confirm.isNullOrEmpty()
    }

    private fun doPasswordsMatch(): Boolean {
        return new.equals(confirm, ignoreCase = false)
    }
}