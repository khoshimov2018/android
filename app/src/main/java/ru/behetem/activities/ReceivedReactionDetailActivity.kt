package ru.behetem.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_my_profile_detail.*
import kotlinx.android.synthetic.main.activity_received_reaction_detail.*
import kotlinx.android.synthetic.main.activity_received_reaction_detail.countLinear
import kotlinx.android.synthetic.main.activity_received_reaction_detail.imageView
import kotlinx.android.synthetic.main.activity_received_reaction_detail.mainLayout
import ru.behetem.R
import ru.behetem.adapters.InterestsAdapter
import ru.behetem.adapters.InterestsWhiteAdapter
import ru.behetem.databinding.ActivityReceivedReactionDetailBinding
import ru.behetem.models.ChatRoomModel
import ru.behetem.models.ReactionModel
import ru.behetem.utils.*
import ru.behetem.viewmodels.ReceivedReactionDetailViewModel

class ReceivedReactionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceivedReactionDetailBinding
    private lateinit var receivedReactionDetailViewModel: ReceivedReactionDetailViewModel

    private var interestsAdapter: InterestsAdapter? = null
    private var interestsWhiteAdapter: InterestsWhiteAdapter? = null

    private val listOfCountViews: MutableList<View> = ArrayList()
    private lateinit var listOfImages: MutableList<String>
    private var currentSelectedIndex = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_received_reaction_detail)
        binding.lifecycleOwner = this
        initViewModel()
        receivedReactionDetailViewModel.getUserDetail()

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

        whiteRecycler.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
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
        receivedReactionDetailViewModel =
            ViewModelProvider(this).get(ReceivedReactionDetailViewModel::class.java)
        binding.viewModel = receivedReactionDetailViewModel

        val loggedInUser = getLoggedInUserFromShared(this)
        receivedReactionDetailViewModel.setLoggedInUser(loggedInUser)

        val comingFrom = intent.getStringExtra(Constants.COMING_FROM)
        comingFrom?.let {
            if (it.equals(Constants.RECEIVED_REACTION, ignoreCase = true)) {
                val receivedReaction =
                    intent.getParcelableExtra<ReactionModel>(Constants.RECEIVED_REACTION)
                receivedReaction?.let { reaction ->
                    receivedReactionDetailViewModel.setReceivedReaction(reaction)
                }
            }
            if (it.equals(Constants.CHAT_ROOM, ignoreCase = true)) {
                val chatRoom = intent.getParcelableExtra<ChatRoomModel>(Constants.CHAT_ROOM)
                chatRoom?.let { chat ->
                    receivedReactionDetailViewModel.setChatRoom(chat)
                }
            }
        }

        initObservers()
    }

    private fun initObservers() {
        receivedReactionDetailViewModel.getUserProfileLiveData().observe(this, {
            if (it != null) {
                setInterests()
                if (it.images != null) {
                    listOfImages = it.images!!
                    initView()
                }
            }
        })

        receivedReactionDetailViewModel.getMoveToMessage().observe(this, {
            if (it) {
                receivedReactionDetailViewModel.setMoveToMessage(false)
                moveToMessage()
            }
        })

        receivedReactionDetailViewModel.getShowActivityPopup().observe(this, {
            if (it) {
                receivedReactionDetailViewModel.setShowActivityPopup(false)
                showActivityStatusPopup(receivedReactionDetailViewModel.getActivityStatus())
            }
        })

        receivedReactionDetailViewModel.getShowOutOfReactionsPopup().observe(this, {
            if (it) {
                receivedReactionDetailViewModel.setShowOutOfReactionsPopup(false)
                showOutOfReactionsPopup()
            }
        })

        receivedReactionDetailViewModel.getMoveBack().observe(this, {
            if (it) {
                receivedReactionDetailViewModel.setMoveBack(false)
                onBackPressed()
            }
        })

        receivedReactionDetailViewModel.getBackButtonClicked()
            .observe(this, Observer { isPressed: Boolean ->
                if (isPressed) {
                    this.onBackPressed()
                }
            })

        receivedReactionDetailViewModel.getShowNoInternet().observe(this, {
            if (it) {
                receivedReactionDetailViewModel.setShowNoInternet(false)
                showInfoAlertDialog(this, getString(R.string.no_internet))
            }
        })

        receivedReactionDetailViewModel.getBaseResponse().observe(this, {
            it?.let {
                receivedReactionDetailViewModel.setBaseResponse(null)
                validateResponse(this, it)
            }
        })
    }

    private fun setInterests() {
        interestsAdapter =
            InterestsAdapter(
                receivedReactionDetailViewModel.getInterestsList(),
                receivedReactionDetailViewModel
            )
        binding.interestsAdapter = interestsAdapter
        interestsAdapter?.notifyDataSetChanged()

        interestsWhiteAdapter =
            InterestsWhiteAdapter(
                receivedReactionDetailViewModel.getInterestsList(),
                receivedReactionDetailViewModel
            )
        binding.interestsWhiteAdapter = interestsWhiteAdapter
        interestsWhiteAdapter?.notifyDataSetChanged()
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
                view.setBackgroundResource(R.color.lineUnselected)

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
                .placeholder(R.drawable.grey_bg)
                .into(imageView);
            listOfCountViews[index].setBackgroundResource(R.color.lineSelected)
        }
    }

    private fun resetCountViews() {
        for (view in listOfCountViews) {
            view.setBackgroundResource(R.color.lineUnselected)
        }
    }

    private fun showActivityStatusPopup(activity: Double) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_activity_status)

        val close = dialog.findViewById<ImageView>(R.id.close)
        val description = dialog.findViewById<TextView>(R.id.description)

        if (activity > 0) {
            description.text = getString(R.string.with_other_people, activity)
        }

        close.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showOutOfReactionsPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_out_of_reactions)

        val close = dialog.findViewById<ImageView>(R.id.close)
        val buyMore = dialog.findViewById<Button>(R.id.buyMore)

        close.setOnClickListener {
            dialog.dismiss()
        }

        buyMore.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun moveToMessage() {
        receivedReactionDetailViewModel.getReceivedReaction()?.let {
            val chatRoomItem = ChatRoomModel()
            chatRoomItem.senderId = it.receiverId
            chatRoomItem.recipientId = it.senderId
            chatRoomItem.receiverName = it.senderName
            chatRoomItem.receiverImage = it.image

            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(Constants.CHAT_ROOM, chatRoomItem)
            startActivity(intent)
            finish()
        }
    }
}