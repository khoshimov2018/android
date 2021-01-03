package com.example.dating.bindingadapters

import androidx.databinding.BindingAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class BindingAdapters {

    companion object {
        @BindingAdapter("onNavigationItemSelected")
        @JvmStatic
        fun setOnNavigationItemSelectedListener(
            view: BottomNavigationView,
            listener: BottomNavigationView.OnNavigationItemSelectedListener?
        ) {
            view.setOnNavigationItemSelectedListener(listener)
        }
    }
}