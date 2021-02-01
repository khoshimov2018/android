package ru.behetem.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ru.behetem.R
import ru.behetem.activities.CoinsActivity
import ru.behetem.activities.MyProfileDetailActivity
import ru.behetem.activities.PremiumActivity
import ru.behetem.activities.SettingsActivity
import ru.behetem.databinding.MyProfileFragmentBinding
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.utils.showInfoAlertDialog
import ru.behetem.utils.validateResponse
import ru.behetem.viewmodels.MyProfileViewModel

class MyProfileFragment : Fragment() {

    companion object {
        fun newInstance() = MyProfileFragment()
        const val MY_PROFILE_DETAIL = 101
    }

    private lateinit var viewModel: MyProfileViewModel
    private lateinit var binding: MyProfileFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.my_profile_fragment, container, false)
        val view: View = binding.root
        initViewModel()

        return view
    }

    private fun initViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.setLoggedInUser(getLoggedInUserFromShared(requireActivity()))

        initObservers()
    }

    private fun initObservers() {
        viewModel.getShowNoInternet().observe(viewLifecycleOwner, {
            if(it) {
                viewModel.setShowNoInternet(false)
                showInfoAlertDialog(requireActivity(), getString(R.string.no_internet))
            }
        })

        viewModel.getBaseResponse().observe(viewLifecycleOwner, {
            it?.let {
                viewModel.setBaseResponse(null)
                validateResponse(requireActivity(), it)
            }
        })

        viewModel.getMoveToSettings().observe(viewLifecycleOwner, {
            if(it) {
                viewModel.setMoveToSettings(false)
                moveToSettings()
            }
        })

        viewModel.getMoveToProfile().observe(viewLifecycleOwner, {
            if(it) {
                viewModel.setMoveToProfile(false)
                moveToProfile()
            }
        })

        viewModel.getMoveToCoins().observe(viewLifecycleOwner, {
            if(it) {
                viewModel.setMoveToCoins(false)
                moveToCoins()
            }
        })

        viewModel.getMoveToPremium().observe(viewLifecycleOwner, {
            if(it) {
                viewModel.setMoveToPremium(false)
                moveToPremium()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserProfile()
    }

    private fun moveToSettings() {
        val intent = Intent(requireActivity(), SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun moveToProfile() {
        val intent = Intent(requireActivity(), MyProfileDetailActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, viewModel.getCurrentUser())

        var arrayList: ArrayList<String>? = null
        if(viewModel.getImages() != null) {
            arrayList = ArrayList(viewModel.getImages()!!)
        }

        intent.putStringArrayListExtra(Constants.USER_IMAGES, arrayList)
        startActivityForResult(intent, MY_PROFILE_DETAIL)
    }

    private fun moveToCoins() {
        val intent = Intent(requireActivity(), CoinsActivity::class.java)
        startActivity(intent)
    }

    private fun moveToPremium() {
        val intent = Intent(requireActivity(), PremiumActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MyProfileDetailActivity.EDIT_PROFILE_ACTIVITY) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val currentUser = data?.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
                currentUser?.let {
                    viewModel.setCurrentUser(it)
                }
            }
        }
    }
}