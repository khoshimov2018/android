package ru.behetem.interfaces

import android.view.View
import ru.behetem.models.ReactionModel

interface IReactionClick {

    fun reactionItemClicked(view: View, reactionItem: ReactionModel)
}