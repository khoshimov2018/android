package ru.behetem.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PremiumViewModel: BaseViewModel() {

    private val currentChosenPlan: MutableLiveData<Int> = MutableLiveData(1)
    private val continueClicked: MutableLiveData<Boolean> = MutableLiveData()

    fun changePlan(view: View, index: Int) {
        currentChosenPlan.value = index
    }

    fun onContinue(view: View) {
        continueClicked.value = true
    }

    fun onCancelled(view: View) {
        setBackButtonClicked(true)
    }

    fun getCurrentChosenPlan(): LiveData<Int> {
        return currentChosenPlan
    }

    fun getContinueClicked(): LiveData<Boolean> {
        return continueClicked
    }

    fun setContinueClicked(clicked: Boolean) {
        continueClicked.value = clicked
    }

    fun getCurrentPlan(): Int {
        return currentChosenPlan.value?.let { it } ?: -1
    }
}