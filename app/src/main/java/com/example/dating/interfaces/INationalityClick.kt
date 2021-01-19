package com.example.dating.interfaces

import android.view.View
import com.example.dating.models.NationalityModel

interface INationalityClick {

    fun nationalityItemClicked(view: View, nationalityItem: NationalityModel)
}