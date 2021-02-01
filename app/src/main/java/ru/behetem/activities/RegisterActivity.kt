package ru.behetem.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_register.*
import ru.behetem.R
import ru.behetem.adapters.RegistrationTabsAdapter
import ru.behetem.utils.Constants
import ru.behetem.utils.dpToPx
import ru.behetem.utils.openUrlInBrowser

class RegisterActivity : AppCompatActivity() {

    private lateinit var registrationTabsAdapter: RegistrationTabsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        privacyTerms.setOnClickListener {
            openPrivacyTerms()
        }

        initViews()
    }

    private fun initViews() {
        registrationTabsAdapter = RegistrationTabsAdapter(this)
        val viewPager = findViewById<ViewPager2>(R.id.pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        viewPager.adapter = registrationTabsAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when(position) {
                0 -> {
                    tab.text = getString(R.string.registration)
                }
                else -> {
                    tab.text = getString(R.string.login)
                }
            }
        }.attach()

        addKeyboardDetectListener()
    }

    private fun addKeyboardDetectListener(){
        val topView = window.decorView.findViewById<View>(android.R.id.content)
        topView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDifference = topView.rootView.height - topView.height
            if(heightDifference > dpToPx(this, 200F)){
                // keyboard shown
                privacyTerms.visibility = View.GONE
            } else {
                // keyboard hidden
                privacyTerms.visibility = View.VISIBLE
            }
        }
    }

    private fun openPrivacyTerms() {
        openUrlInBrowser(this, Constants.PRIVACY_URL)
    }
}