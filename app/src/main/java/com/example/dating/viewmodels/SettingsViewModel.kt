package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingsViewModel: BaseViewModel() {

    private val moveToCoins: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToPremium: MutableLiveData<Boolean> = MutableLiveData()

    fun coinsClicked(view: View) {
        moveToCoins.value = true
    }

    fun premiumClicked(view: View) {
        moveToPremium.value = true
    }

    fun getMoveToCoins(): LiveData<Boolean> {
        return moveToCoins
    }

    fun setMoveToCoins(move: Boolean) {
        moveToCoins.value = move
    }

    fun getMoveToPremium(): LiveData<Boolean> {
        return moveToPremium
    }

    fun setMoveToPremium(move: Boolean) {
        moveToPremium.value = move
    }
}