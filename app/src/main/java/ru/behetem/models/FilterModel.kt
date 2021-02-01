package ru.behetem.models

import ru.behetem.utils.Constants
import ru.behetem.utils.Gender

data class FilterModel(
    var dateOfBirthFrom: String? = null,
    var dateOfBirthTo: String? = null,
    var gender: String? = null,
    var growthFrom: Int? = null,
    var growthTo: Int? = null,
    var interest: MutableList<String>? = null,
    var maxDistance: Int? = null,
    var nationality: MutableList<String>? = null,
    var weightFrom: Int? = null,
    var weightTo: Int? = null,
    var page: Int = 1,
    var size: Int = Constants.PAGE_SIZE,
    var ageFrom: Int? = null,
    var ageTo: Int? = null
) {
    fun isLookingForMale(): Boolean {
        return gender == Gender.MALE
    }

    fun isLookingForFemale(): Boolean {
        return gender == Gender.FEMALE
    }

    fun isLookingForSelected(): Boolean {
        return gender != null
    }

    fun getFromToAge(): String {
        return if (ageFrom == null) ""
        else "${ageFrom!!} - ${ageTo!!}"
    }

    fun getAgeFrom(): Int {
        return if(ageFrom == null) 0 else ageFrom!! - Constants.MIN_AGE_FOR_CAL
    }

    fun getAgeTo(): Int {
        return if(ageTo == null) 82 else ageTo!! - Constants.MIN_AGE_FOR_CAL
    }
}