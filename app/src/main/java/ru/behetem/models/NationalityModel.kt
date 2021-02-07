package ru.behetem.models

import ru.behetem.utils.Gender

data class NationalityModel(
    var label: String? = null,
    var male: String? = null,
    var female: String? = null,
    var isSelected: Boolean? = null
) {
    fun getLabelToShow(chosenGender: String): String {
        return when (chosenGender) {
            Gender.MALE -> {
                getMaleLabel()
            }
            Gender.FEMALE -> {
                getFemaleLabel()
            }
            else -> {
                ""
            }
        }
    }

    fun ifNationalityMatches(nationality: String): Boolean {
        return label.equals(nationality, ignoreCase = true) || male.equals(nationality, ignoreCase = true) || female.equals(nationality, ignoreCase = true)
    }

    private fun getMaleLabel(): String {
        return if (male.isNullOrEmpty()) {
            if (label.isNullOrEmpty()) {
                ""
            }
            else {
                label!!
            }
        } else {
            male!!
        }
    }

    private fun getFemaleLabel(): String {
        return if (female.isNullOrEmpty()) {
            if (label.isNullOrEmpty()) {
                ""
            }
            else {
                label!!
            }
        } else {
            female!!
        }
    }
}