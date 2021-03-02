package ru.behetem.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_my_profile_detail.*
import ru.behetem.R
import ru.behetem.adapters.InterestsAdapter
import ru.behetem.databinding.ActivityMyProfileDetailBinding
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants
import ru.behetem.utils.dpToPx
import ru.behetem.utils.printLog
import ru.behetem.viewmodels.MyProfileDetailViewModel

class MyProfileDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileDetailBinding
    private lateinit var myProfileDetailViewModel: MyProfileDetailViewModel

    private val listOfCountViews: MutableList<View> = ArrayList()
    private lateinit var listOfImages: MutableList<String>
    private var currentSelectedIndex = 0

    private var interestsAdapter: InterestsAdapter? = null

    companion object {
        const val EDIT_PROFILE_ACTIVITY = 101
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile_detail)
        binding.lifecycleOwner = this

        initViewModel()

        imageView.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
            val halfOfScreen = mainLayout.width / 2
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    val position = motionEvent.x
                    if (position < halfOfScreen) {
                        // go to previous
                        if (currentSelectedIndex > 0) {
                            showIndex(--currentSelectedIndex)
                        }
                    } else {
                        // go to next
                        if (currentSelectedIndex < listOfImages.size - 1) {
                            showIndex(++currentSelectedIndex)
                        }
                    }
                }
            }
            return@OnTouchListener true
        })
    }

    private fun initViewModel() {
        myProfileDetailViewModel = ViewModelProvider(this).get(MyProfileDetailViewModel::class.java)
        binding.viewModel = myProfileDetailViewModel

        val profileUser = intent.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
        profileUser?.let {
            myProfileDetailViewModel.setCurrentUser(it)
            setInterests()
        }
        val imagesList = intent.getStringArrayListExtra(Constants.USER_IMAGES)
        imagesList?.let {
            myProfileDetailViewModel.setImagesListLiveData(it)
            listOfImages = it
            initView()
        }

        initObservers()
    }

    private fun initObservers() {
        myProfileDetailViewModel.getMoveToEditProfile()
            .observe(this, Observer {
                if (it) {
                    myProfileDetailViewModel.setMoveToEditProfile(false)
                    moveToEditProfile()
                }
            })

        myProfileDetailViewModel.getBackButtonClicked()
            .observe(this, Observer { isPressed: Boolean ->
                if (isPressed) {
                    this.onBackPressed()
                }
            })
    }

    private fun initView() {
        if (this::listOfImages.isInitialized) {
            countLinear.removeAllViews()
            listOfCountViews.clear()

            for (index in 0 until listOfImages.size) {
                val view = View(this)
                val layoutParams: LinearLayout.LayoutParams =
                    LinearLayout.LayoutParams(0, dpToPx(this, 2F).toInt(), 1F)
                layoutParams.setMargins(dpToPx(this, 5F).toInt(), 0, dpToPx(this, 5F).toInt(), 0)
                view.layoutParams = layoutParams
                view.setBackgroundResource(R.color.lightGreyColor)

                countLinear.addView(view)
                listOfCountViews.add(view)
            }

            showIndex(currentSelectedIndex)
        }
    }

    private fun showIndex(index: Int) {
        if (index < listOfImages.size) {
            resetCountViews()
//        imageView.setBackgroundResource(listOfImages[index])
            Glide.with(this)
                .load(listOfImages[index])
                .placeholder(R.drawable.logo)
                .into(imageView);
            listOfCountViews[index].setBackgroundResource(R.color.red)
        }
    }

    private fun resetCountViews() {
        for (view in listOfCountViews) {
            view.setBackgroundResource(R.color.lightGreyColor)
        }
    }

    private fun moveToEditProfile() {
        val intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra(Constants.PROFILE_USER, myProfileDetailViewModel.getCurrentUser())

        var arrayList: ArrayList<String>? = null
        if (myProfileDetailViewModel.getImages() != null) {
            arrayList = ArrayList(myProfileDetailViewModel.getImages()!!)
        }

        intent.putStringArrayListExtra(Constants.USER_IMAGES, arrayList)

        startActivityForResult(intent, EDIT_PROFILE_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                val currentUser = data?.getParcelableExtra<UserModel>(Constants.PROFILE_USER)
                currentUser?.let {
                    myProfileDetailViewModel.setCurrentUser(it)
                    setInterests()
                }
                val imagesList = data?.getStringArrayListExtra(Constants.USER_IMAGES)
                imagesList?.let {
                    myProfileDetailViewModel.setImagesListLiveData(it)
                    listOfImages = it
                    initView()
                }
            }
        }
    }

    private fun setInterests() {
        interestsAdapter =
            InterestsAdapter(myProfileDetailViewModel.getInterestsList(), myProfileDetailViewModel)
        binding.interestsAdapter = interestsAdapter
        interestsAdapter?.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        returnIntent.putExtra(Constants.PROFILE_USER, myProfileDetailViewModel.getCurrentUser())

        var arrayList: java.util.ArrayList<String>? = null
        if(myProfileDetailViewModel.getImages() != null) {
            arrayList = java.util.ArrayList(myProfileDetailViewModel.getImages()!!)
        }
        returnIntent.putStringArrayListExtra(Constants.USER_IMAGES, arrayList)

        setResult(RESULT_OK, returnIntent)
        super.onBackPressed()
    }
}