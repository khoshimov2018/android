package ru.behetem.viewmodels

import android.view.View

class PremiumViewModel: BaseViewModel() {

    fun onCancelled(view: View) {
        setBackButtonClicked(true)
    }
}