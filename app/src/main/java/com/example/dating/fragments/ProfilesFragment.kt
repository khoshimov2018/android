package com.example.dating.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.dating.R
import com.example.dating.databinding.ProfilesFragmentBinding
import com.example.dating.models.UserModel
import com.example.dating.utils.*
import com.example.dating.viewmodels.ProfilesViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import it.sephiroth.android.library.rangeseekbar.RangeSeekBar

class ProfilesFragment : Fragment() {

    companion object {
        fun newInstance() = ProfilesFragment()
        private const val LOCATION_PERMISSION = 201
    }

    private lateinit var viewModel: ProfilesViewModel
    private lateinit var binding: ProfilesFragmentBinding

    private lateinit var viewPager: ViewPager2
    private var comingFromSettings = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfilesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.profiles_fragment, container, false)
        val view: View = binding.root
        viewPager = view.findViewById(R.id.pager)

        initViewModel()

        return view
    }

    private fun initViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initObservers()

        viewModel.setLoggedInUser(getLoggedInUserFromShared(requireActivity()))
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fetchLocation()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION
                    )
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION
                    )
                }
            }
        } else {
            fetchLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation()
                } else {
                    showLocationPopup()
                }
            }
        }
    }

    private fun showLocationPopup() {
        val alertBuilder = AlertDialog.Builder(requireActivity())
        alertBuilder.setCancelable(false)
        alertBuilder.setMessage(getString(R.string.need_location_permission))

        alertBuilder.setPositiveButton(
            getString(R.string.ok),
            DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.cancel()
                openSettings()
            })

        val alertDialog = alertBuilder.create()
        alertDialog.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        comingFromSettings = true
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (comingFromSettings) {
            comingFromSettings = false
            checkLocationPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        viewModel.setLoaderVisible(true)

        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder =
            locationRequest?.let { LocationSettingsRequest.Builder().addLocationRequest(it) }

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder?.build())

        task.addOnSuccessListener { _ ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (location in locationResult.locations) {
                        viewModel.setLoaderVisible(false)
                        viewModel.setLatLong(location.latitude, location.longitude)
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                        break
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        task.addOnFailureListener { exception ->
            viewModel.setLoaderVisible(false)

            if (exception is ResolvableApiException) {
                try {
                    val resolvable: ResolvableApiException = exception
                    startIntentSenderForResult(
                        resolvable.resolution.intentSender,
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        null,
                        0,
                        0,
                        0,
                        null
                    );
                } catch (e: IntentSender.SendIntentException) {
                    couldNotGetLocationPopup()
                }
            } else {
                couldNotGetLocationPopup()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LocationRequest.PRIORITY_HIGH_ACCURACY -> {
                if (resultCode == Activity.RESULT_OK) {
                    fetchLocation()
                } else {
                    couldNotGetLocationPopup()
                }
            }
        }
    }

    private fun couldNotGetLocationPopup() {
        val alertBuilder = AlertDialog.Builder(requireActivity())
        alertBuilder.setCancelable(false)
        alertBuilder.setMessage(getString(R.string.could_not_get_location))

        alertBuilder.setPositiveButton(getString(R.string.ok)) { dialogInterface, _ ->
            dialogInterface.cancel()
            requireActivity().finishAffinity()
        }

        val alertDialog = alertBuilder.create()
        alertDialog.show()
    }

    private fun initObservers() {
        viewModel.getFilterModelLiveData().observe(viewLifecycleOwner, {
            if (it != null) {
//                viewModel.getUsers()
            }
        })

        viewModel.getUsersListLiveData().observe(viewLifecycleOwner, {
            if (it != null) {
                val pagerAdapter = ScreenSlidePagerAdapter(requireActivity(), it)
                viewPager.adapter = pagerAdapter
            }
        })

        viewModel.getShowFiltersLiveData().observe(viewLifecycleOwner, {
            if (it) {
                viewModel.setShowFiltersLiveData(false)

                val fragmentManager = childFragmentManager
                val filtersDialogFragment = FiltersDialogFragment(viewModel)
                filtersDialogFragment.show(fragmentManager, null)
            }
        })

        viewModel.getShowNoInternet().observe(viewLifecycleOwner, {
            if (it) {
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
    }

    private inner class ScreenSlidePagerAdapter(
        fa: FragmentActivity,
        val usersList: MutableList<UserModel>
    ) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = usersList.size

        override fun createFragment(position: Int): Fragment {
            return UserProfileFragment.newInstance(usersList.get(position))
        }
    }
}