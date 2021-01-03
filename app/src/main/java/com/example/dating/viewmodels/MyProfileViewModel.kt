package com.example.dating.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyProfileViewModel : ViewModel() {

    private val moveToSettings: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToProfile: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToCoins: MutableLiveData<Boolean> = MutableLiveData()
    private val moveToPremium: MutableLiveData<Boolean> = MutableLiveData()

    fun settingsClicked(view: View) {
        moveToSettings.value = true
    }

    fun profileClicked(view: View) {
        moveToProfile.value = true
    }

    fun coinsClicked(view: View) {
        moveToCoins.value = true
    }

    fun premiumClicked(view: View) {
        moveToPremium.value = true
    }

    fun getMoveToSettings(): LiveData<Boolean> {
        return moveToSettings
    }

    fun setMoveToSettings(move: Boolean) {
        moveToSettings.value = move
    }

    fun getMoveToProfile(): LiveData<Boolean> {
        return moveToProfile
    }

    fun setMoveToProfile(move: Boolean) {
        moveToProfile.value = move
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