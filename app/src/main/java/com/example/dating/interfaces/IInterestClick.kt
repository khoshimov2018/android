package com.example.dating.interfaces

import android.view.View
import com.example.dating.models.InterestModel

interface IInterestClick {

    fun interestItemClicked(view: View, interestItem: InterestModel)
}