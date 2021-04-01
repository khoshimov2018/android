package ru.behetem.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PremiumViewModel: BaseViewModel() {

    private val currentChosenPlan: MutableLiveData<Int> = MutableLiveData(1)

    fun changePlan(view: View, index: Int) {
        currentChosenPlan.value = index
    }

    fun onCancelled(view: View) {
        setBackButtonClicked(true)
    }

    fun getCurrentChosenPlan(): LiveData<Int> {
        return currentChosenPlan
    }
}