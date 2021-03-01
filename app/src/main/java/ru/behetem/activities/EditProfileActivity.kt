package ru.behetem.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.user_profile_fragment.*
import ru.behetem.R
import ru.behetem.adapters.InterestsAdapter
import ru.behetem.adapters.NationalitiesAdapter
import ru.behetem.adapters.NationalitiesSpinnerAdapter
import ru.behetem.databinding.ActivityEditProfileBinding
import ru.behetem.models.UserModel
import ru.behetem.utils.*
import ru.behetem.viewmodels.EditProfileViewModel
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var editProfileViewModel: EditProfileViewModel

    private var interestsAdapter: InterestsAdapter? = null

    //    private var nationalitiesAdapter: NationalitiesAdapter? = null
    private var nationalitiesSpinnerAdapter: NationalitiesSpinnerAdapter? = null

    private lateinit var growthWeightBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var careerBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        binding.lifecycleOwner = this
        initViewModel()

        growthWeightBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetForGrowthWeight)
        careerBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetForCareer)
        growthWeightBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    bg.visibility = View.GONE
                else
                    bg.visibility = View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        careerBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    bg.visibility = View.GONE
                else
                    bg.visibility = View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        bg.setOnClickListener {
            growthWeightBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            careerBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun initViewModel() {
        editProfileViewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        binding.viewModel = editProfileViewModel

        editProfileViewModel.setLoggedInUser(getLoggedInUserFromShared(this))

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            editProfileViewModel.setCurrentUser(it)
        }
        val imagesList = intent.getStringArrayListExtra(Constants.USER_IMAGES)
        imagesList?.let {
            editProfileViewModel.setImagesListLiveData(it)
        }

        editProfileViewModel.getInterests()
        editProfileViewModel.getNationalities()

        initObservers()
    }

    private fun initObservers() {
        editProfileViewModel.getOpenImagePicker().observe(this, Observer {
            if (it) {
                editProfileViewModel.setOpenImagePicker(false)
                openImagePicker()
            }
        })

        editProfileViewModel.getErrorResId().observe(this, {
            if (it != null) {
                editProfileViewModel.setErrorResId(null)
                showInfoAlertDialog(this, getString(it))
            }
        })

        editProfileViewModel.getShowDatePicker().observe(this, Observer {
            if (it) {
                editProfileViewModel.setShowDatePicker(false)
                showDatePicker()
            }
        })

        editProfileViewModel.getInterestsList().observe(this, {
            if (it != null) {
                if (it.isEmpty()) {
                    showInfoAlertDialog(this, getString(R.string.no_interests))
                } else {
                    interestsAdapter = InterestsAdapter(it, editProfileViewModel)
                    binding.interestsAdapter = interestsAdapter
                    interestsAdapter?.notifyDataSetChanged()
                }
            } else {
                interestsAdapter = null
            }
        })

        editProfileViewModel.getNationalitiesList().observe(this, {
            if (it != null) {
                if (it.isEmpty()) {
                    showInfoAlertDialog(this, getString(R.string.no_nationalities))
                } else {
                    /*nationalitiesAdapter = NationalitiesAdapter(
                        it,
                        editProfileViewModel,
                        editProfileViewModel.getChosenGender()
                    )
                    binding.nationalitiesAdapter = nationalitiesAdapter
                    nationalitiesAdapter?.notifyDataSetChanged()*/
                    nationalitiesSpinnerAdapter = NationalitiesSpinnerAdapter(
                        this,
                        R.layout.adapter_nationality_spinner_item,
                        it
                    )
                    binding.nationalitiesSpinnerAdapter = nationalitiesSpinnerAdapter
                    nationalitiesSpinnerAdapter?.notifyDataSetChanged()
                }
            } else {
//                nationalitiesAdapter = null
                nationalitiesSpinnerAdapter = null
            }
        })

        editProfileViewModel.getAllowToGoBack().observe(this, {
            if (it) {
                moveUserBack()
            }
        })

        editProfileViewModel.getShowGrowthWeightBottomSheet().observe(this, {
            if (it) {
                editProfileViewModel.setShowGrowthWeightBottomSheet(false)
                growthWeightBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })

        editProfileViewModel.getShowCareerBottomSheet().observe(this, {
            if (it) {
                editProfileViewModel.setShowCareerBottomSheet(false)
                careerBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })

        editProfileViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        editProfileViewModel.getShowNoInternet().observe(this, {
            if (it) {
                editProfileViewModel.setShowNoInternet(false)
                showInfoAlertDialog(this, getString(R.string.no_internet))
            }
        })

        editProfileViewModel.getBaseResponse().observe(this, {
            it?.let {
                editProfileViewModel.setBaseResponse(null)
                validateResponse(this, it)
            }
        })
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val picker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                run {
                    editProfileViewModel.setDateOfBirth(selectedYear, selectedMonth, selectedDay)
                }
            }, year, month, day
        )
        picker.datePicker.maxDate = c.timeInMillis
        picker.show()
    }

    private fun openImagePicker() {
        ImagePicker.with(this)
            .crop(9f, 16f)
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val fileUri: Uri? = data?.data

                when (editProfileViewModel.getCurrentImageForPosition()) {
                    0 -> {
                        image1.setImageURI(fileUri)
                    }
                    1 -> {
                        image2.setImageURI(fileUri)
                    }
                    2 -> {
                        image3.setImageURI(fileUri)
                    }
                    3 -> {
                        image4.setImageURI(fileUri)
                    }
                    4 -> {
                        image5.setImageURI(fileUri)
                    }
                    5 -> {
                        image6.setImageURI(fileUri)
                    }
                }

                fileUri?.let {
                    editProfileViewModel.setImageUri(it)
                }
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                // User cancelled
                // Do nothing
            }
        }
    }

    override fun onBackPressed() {
        if (isInternetAvailable(this)) {
            if (editProfileViewModel.updateProfile()) {
                moveUserBack()
            }
        } else {
            moveUserBack()
        }
    }

    private fun moveUserBack() {
        val returnIntent = Intent()
        returnIntent.putExtra(Constants.PROFILE_USER, editProfileViewModel.getCurrentUser())
        setResult(RESULT_OK, returnIntent)
        super.onBackPressed()
    }
}