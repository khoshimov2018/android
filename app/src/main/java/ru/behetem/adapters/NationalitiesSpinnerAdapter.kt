package ru.behetem.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ru.behetem.models.NationalityModel

class NationalitiesSpinnerAdapter(
    context: Context,
    private val layoutResource: Int,
    private val nationalities: MutableList<NationalityModel>
) :
    ArrayAdapter<NationalityModel>(context, layoutResource, nationalities) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context)
            .inflate(layoutResource, parent, false) as TextView
        view.text = nationalities[position].label
        return view
    }
}