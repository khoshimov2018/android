package ru.behetem.viewmodels

import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.behetem.R
import ru.behetem.utils.BottomTabs

class HomeViewModel: ViewModel() {

    private val tabChanged: MutableLiveData<Int> = MutableLiveData()

    fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_my_profile -> {
                tabChanged.value = BottomTabs.MY_PROFILE_TAB
                true
            }
            R.id.action_profiles -> {
                tabChanged.value = BottomTabs.PROFILES_TAB
                true
            }
            R.id.action_messenger -> {
                tabChanged.value = BottomTabs.MESSENGER_TAB
                true
            }
            else -> false
        }
    }

    fun getTabChanged(): LiveData<Int> {
        return tabChanged
    }
}