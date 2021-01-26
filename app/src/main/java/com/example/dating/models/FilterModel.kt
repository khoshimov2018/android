package com.example.dating.models

import com.example.dating.utils.Constants
import com.example.dating.utils.Gender

data class FilterModel(
    var dateOfBirthFrom: String? = null,
    var dateOfBirthTo: String? = null,
    var gender: String? = null,
    var growthFrom: Int? = null,
    var growthTo: Int? = null,
    var interests: MutableList<String>? = null,
    var maxDistance: Int? = null,
    var nationalities: MutableList<String>? = null,
    var weightFrom: Int? = null,
    var weightTo: Int? = null,
    var pageNumber: Int = 1,
    var pageSize: Int = Constants.PAGE_SIZE
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
}