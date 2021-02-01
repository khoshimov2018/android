package ru.behetem.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_home.*
import ru.behetem.R
import ru.behetem.databinding.ActivityHomeBinding
import ru.behetem.fragments.MessengerFragment
import ru.behetem.fragments.MyProfileFragment
import ru.behetem.fragments.ProfilesFragment
import ru.behetem.utils.BottomTabs
import ru.behetem.viewmodels.HomeViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    private val myProfileFragment = MyProfileFragment.newInstance()
    private val profilesFragment = ProfilesFragment.newInstance()
    private val messengerFragment = MessengerFragment.newInstance()

    private var currentFragment: Fragment? = null

    companion object {
        private const val MY_PROFILE_FRAGMENT_TAG = "MY_PROFILE_FRAGMENT_TAG"
        private const val PROFILES_FRAGMENT_TAG = "PROFILES_FRAGMENT_TAG"
        private const val MESSENGER_FRAGMENT_TAG = "MESSENGER_FRAGMENT_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding.viewModel = homeViewModel

        initObservers()
    }

    private fun initObservers() {
        homeViewModel.getTabChanged().observe(this, Observer {
            when (it) {
                BottomTabs.MY_PROFILE_TAB -> {
                    bottomNavigationView.setBackgroundResource(R.drawable.bottom_tabs_background)
                    changeIconTintColor(false)
                    showMyProfileTab()
                }
                BottomTabs.PROFILES_TAB -> {
                    bottomNavigationView.setBackgroundResource(R.drawable.bottom_tabs_profiles_background)
                    changeIconTintColor(true)
                    showProfilesTab()
                }
                BottomTabs.MESSENGER_TAB -> {
                    bottomNavigationView.setBackgroundResource(R.drawable.bottom_tabs_background)
                    changeIconTintColor(false)
                    showMessengerTab()
                }
            }
        })
        bottomNavigationView.selectedItemId = R.id.action_profiles
        showProfilesTab()
    }

    @Suppress("DEPRECATION")
    private fun changeIconTintColor(isWhite: Boolean) {
        if(isWhite) {
            if(Build.VERSION.SDK_INT >= 23){
                bottomNavigationView.itemIconTintList = resources.getColorStateList(R.color.bottom_navigation_color_white, null)
            } else {
                bottomNavigationView.itemIconTintList = resources.getColorStateList(R.color.bottom_navigation_color_white)
            }
        } else {
            if(Build.VERSION.SDK_INT >= 23){
                bottomNavigationView.itemIconTintList = resources.getColorStateList(R.color.bottom_navigation_color, null)
            } else {
                bottomNavigationView.itemIconTintList = resources.getColorStateList(R.color.bottom_navigation_color)
            }
        }
    }

    private fun showMyProfileTab() {
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag(MY_PROFILE_FRAGMENT_TAG)
        if (fragment == null) {
            supportFragmentManager.commit {
                add(R.id.frameLayout, myProfileFragment, MY_PROFILE_FRAGMENT_TAG)
            }
        }
        supportFragmentManager.commit {
            currentFragment?.let {
                detach(it)
            }
            attach(myProfileFragment)
            currentFragment = myProfileFragment
        }
    }

    private fun showProfilesTab() {
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag(PROFILES_FRAGMENT_TAG)
        if (fragment == null) {
            supportFragmentManager.commit {
                add(R.id.frameLayout, profilesFragment, PROFILES_FRAGMENT_TAG)
            }
        }
        supportFragmentManager.commit {
            currentFragment?.let {
                detach(it)
            }
            attach(profilesFragment)
            currentFragment = profilesFragment
        }
    }

    private fun showMessengerTab() {
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag(MESSENGER_FRAGMENT_TAG)
        if (fragment == null) {
            supportFragmentManager.commit {
                add(R.id.frameLayout, messengerFragment, MESSENGER_FRAGMENT_TAG)
            }
        }
        supportFragmentManager.commit {
            currentFragment?.let {
                detach(it)
            }
            attach(messengerFragment)
            currentFragment = messengerFragment
        }
    }
}