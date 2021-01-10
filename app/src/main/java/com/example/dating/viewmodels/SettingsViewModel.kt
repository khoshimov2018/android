package com.example.dating.viewmodels

import android.content.DialogInterface
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dating.R
import com.example.dating.utils.logoutAndMoveToHome
import com.example.dating.utils.showAlertDialog

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

    fun onLogoutClicked(view: View){
        val context = view.context
        showAlertDialog(context,
            null,
            context.getString(R.string.sure_logout),
            context.getString(R.string.yes),
            DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.cancel()
                logoutAndMoveToHome(context)
            },
            context.getString(R.string.no),
            null
        )
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