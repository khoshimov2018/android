package ru.behetem.interfaces

import android.view.View
import ru.behetem.models.NationalityModel

interface INationalityClick {

    fun nationalityItemClicked(view: View, nationalityItem: NationalityModel)
}