package com.example.dating.viewmodels

import android.content.DialogInterface
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dating.R
import com.example.dating.utils.logoutAndMoveToHome
import com.example.dating.utils.showAlertDialog

class SettingsViewModel: BaseViewModel() {

    private val versionName: MutableLiveData<String> = MutableLiveData()
    private val aboutUsClicked: MutableLiveData<Boolean> = MutableLiveData()
    private val helpClicked: MutableLiveData<Boolean> = MutableLiveData()
    private val feedbackClicked: MutableLiveData<Boolean> = MutableLiveData()

    fun onAboutUsClicked(view: View) {
        aboutUsClicked.value = true
    }

    fun onHelpClicked(view: View) {
        helpClicked.value = true
    }

    fun onFeedbackClicked(view: View) {
        feedbackClicked.value = true
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

    fun getVersionName(): LiveData<String> {
        return versionName
    }

    fun setVersionName(versionName: String) {
        this.versionName.value = versionName
    }

    fun getAboutUsClicked(): LiveData<Boolean> {
        return aboutUsClicked
    }

    fun setAboutUsClicked(clicked: Boolean) {
        aboutUsClicked.value = clicked
    }

    fun getHelpClicked(): LiveData<Boolean> {
        return helpClicked
    }

    fun setHelpClicked(clicked: Boolean) {
        helpClicked.value = clicked
    }

    fun getFeedbackClicked(): LiveData<Boolean> {
        return feedbackClicked
    }

    fun setFeedbackClicked(clicked: Boolean) {
        feedbackClicked.value = clicked
    }
}