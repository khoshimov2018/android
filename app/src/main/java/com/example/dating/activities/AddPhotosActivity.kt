package com.example.dating.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dating.R
import com.example.dating.databinding.ActivityAddPhotosBinding
import com.example.dating.databinding.ActivityChooseGenderBinding
import com.example.dating.models.UserModel
import com.example.dating.utils.Constants
import com.example.dating.utils.getLoggedInUserFromShared
import com.example.dating.utils.showInfoAlertDialog
import com.example.dating.viewmodels.AddPhotosViewModel
import com.example.dating.viewmodels.ChooseGenderViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_add_photos.*
import java.io.File

class AddPhotosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPhotosBinding
    private lateinit var addPhotosViewModel: AddPhotosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_photos)
        binding.lifecycleOwner = this
        initViewModel()
    }

    private fun initViewModel() {
        addPhotosViewModel = ViewModelProvider(this).get(AddPhotosViewModel::class.java)
        binding.viewModel = addPhotosViewModel

        val loggedInUser = getLoggedInUserFromShared(this)
        addPhotosViewModel.setLoggedInUser(loggedInUser)

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            addPhotosViewModel.setCurrentUser(it)
        }

        initObservers()
    }

    private fun initObservers() {
        addPhotosViewModel.getOpenImagePicker().observe(this, Observer {
            if(it) {
                addPhotosViewModel.setOpenImagePicker(false)
                openImagePicker()
            }
        })

        addPhotosViewModel.getShowLessImagesError().observe(this, Observer {
            if(it) {
                addPhotosViewModel.setShowLessImagesError(false)
                showError()
            }
        })

        addPhotosViewModel.getBackButtonClicked().observe(this, Observer { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        addPhotosViewModel.getMoveFurther().observe(this, Observer {
            if (it) {
                addPhotosViewModel.setMoveFurther(false)
                moveFurther()
            }
        })
    }

    private fun moveFurther() {
        val intent = Intent(this, ChooseLookingForActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, addPhotosViewModel.getCurrentUser())
        startActivity(intent)
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

                when(addPhotosViewModel.getCurrentImageForPosition()){
                    1 -> {
                        image1.setImageURI(fileUri)
                    }
                    2 -> {
                        image2.setImageURI(fileUri)
                    }
                    3 -> {
                        image3.setImageURI(fileUri)
                    }
                    4 -> {
                        image4.setImageURI(fileUri)
                    }
                    5 -> {
                        image5.setImageURI(fileUri)
                    }
                    6 -> {
                        image6.setImageURI(fileUri)
                    }
                }

                fileUri?.let {
                    addPhotosViewModel.setImageUri(it)
                }

//                val file: File = ImagePicker.getFile(data)!!
//                val filePath:String = ImagePicker.getFilePath(data)!!
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

    private fun showError() {
        showInfoAlertDialog(this, getString(R.string.choose_from_3_to_6_photo))
    }
}