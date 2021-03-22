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
    var page: Int = 0,
    var size: Int = Constants.PAGE_SIZE,
    var ageFrom: Int? = null,
    var ageTo: Int? = null,
    var bodyType: MutableList<String>? = null,
    var childrenDesire: Boolean? = null,
    var childrenPresence: MutableList<String>? = null,
    var educationLevel: MutableList<String>? = null,
    var languageKnowledge: MutableList<String>? = null,
    var religionRespect: MutableList<String>? = null,
    var status: MutableList<String>? = null,
    var traditionsRespect: MutableList<String>? = null,
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

    fun getChosenGrowthFrom(): Int {
        return if(growthFrom == null) 0 else growthFrom!!
    }

    fun getChosenGrowthTo(): Int {
        return if(growthTo == null) Constants.MAX_GROWTH_WEIGHT else growthTo!!
    }

    fun getChosenWeightFrom(): Int {
        return if(weightFrom == null) 0 else weightFrom!!
    }

    fun getChosenWeightTo(): Int {
        return if(weightTo == null) Constants.MAX_GROWTH_WEIGHT else weightTo!!
    }
}