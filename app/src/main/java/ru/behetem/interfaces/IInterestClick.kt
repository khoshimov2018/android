package ru.behetem.interfaces

import android.view.View
import ru.behetem.models.InterestModel

interface IInterestClick {

    fun interestItemClicked(view: View, interestItem: InterestModel)
}