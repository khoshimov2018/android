package ru.behetem.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_edit_profile.*
import ru.behetem.R
import ru.behetem.adapters.InterestsAdapter
import ru.behetem.adapters.NationalitiesAdapter
import ru.behetem.databinding.ActivityEditProfileBinding
import ru.behetem.models.UserModel
import ru.behetem.utils.*
import ru.behetem.viewmodels.EditProfileViewModel
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var editProfileViewModel: EditProfileViewModel

    private var interestsAdapter: InterestsAdapter? = null
    private var nationalitiesAdapter: NationalitiesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        binding.lifecycleOwner = this
        initViewModel()
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
            if(it) {
                editProfileViewModel.setOpenImagePicker(false)
                openImagePicker()
            }
        })

        editProfileViewModel.getErrorResId().observe(this, {
            if(it != null) {
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
            if(it != null) {
                if(it.isEmpty()) {
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
            if(it != null) {
                if(it.isEmpty()) {
                    showInfoAlertDialog(this, getString(R.string.no_nationalities))
                } else {
                    nationalitiesAdapter = NationalitiesAdapter(it, editProfileViewModel)
                    binding.nationalitiesAdapter = nationalitiesAdapter
                    nationalitiesAdapter?.notifyDataSetChanged()
                }
            } else {
                nationalitiesAdapter = null
            }
        })

        editProfileViewModel.getAllowToGoBack().observe(this, {
            if(it) {
                moveUserBack()
            }
        })

        editProfileViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
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
        printLog("******** open image")
        ImagePicker.with(this)
            .crop(9f, 16f)
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        printLog("******** on activity")
        when (resultCode) {
            Activity.RESULT_OK -> {
                val fileUri: Uri? = data?.data

                when(editProfileViewModel.getCurrentImageForPosition()){
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
            if(editProfileViewModel.updateProfile()) {
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