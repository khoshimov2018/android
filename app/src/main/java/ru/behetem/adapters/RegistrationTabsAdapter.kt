package ru.behetem.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.behetem.fragments.LoginFragment
import ru.behetem.fragments.RegistrationFragment

class RegistrationTabsAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                RegistrationFragment.newInstance()
            }
            else -> {
                LoginFragment.newInstance()
            }
        }
    }
}