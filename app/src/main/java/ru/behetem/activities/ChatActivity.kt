package ru.behetem.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_chat.*
import ru.behetem.R
import ru.behetem.adapters.ChatMessagesAdapter
import ru.behetem.databinding.ActivityChatBinding
import ru.behetem.models.ChatRoomModel
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.utils.showInfoAlertDialog
import ru.behetem.utils.validateResponse
import ru.behetem.viewmodels.ChatViewModel
import ru.behetem.viewmodels.ChooseLookingForViewModel

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private var chatMessagesAdapter: ChatMessagesAdapter? = null
    private lateinit var recyclerView: RecyclerView
    private var shouldMoveToBottom = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        binding.lifecycleOwner = this
        recyclerView = findViewById(R.id.recyclerView)
        initViewModel()
        addListeners()
    }

    private fun initViewModel() {
        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        binding.viewModel = chatViewModel

        val loggedInUser = getLoggedInUserFromShared(this)
        chatViewModel.setLoggedInUser(loggedInUser)

        val chatRoom = intent.getParcelableExtra<ChatRoomModel>(Constants.CHAT_ROOM)
        chatRoom?.let {
            chatViewModel.setChatRoomLiveData(it)
            chatViewModel.getLatestMessages()
        }

        initObservers()
    }

    private fun initObservers() {
        chatViewModel.getChatsListingLiveData().observe(this, {
            if(it != null) {
                if(chatMessagesAdapter == null) {
                    chatMessagesAdapter = ChatMessagesAdapter(it, chatViewModel)
                    binding.chatMessagesAdapter = chatMessagesAdapter
                }
                chatMessagesAdapter?.notifyDataSetChanged()
            } else {
                chatMessagesAdapter = null
                binding.chatMessagesAdapter = chatMessagesAdapter
            }
        })

        chatViewModel.getShouldMoveToBottom()
            .observe(this, Observer { shouldMove: Boolean ->
                if (shouldMove) {
                    chatViewModel.setShouldMoveToBottom(false)
                    if (this::recyclerView.isInitialized) {
                        if (recyclerView.adapter != null && recyclerView.adapter?.itemCount != null && recyclerView.adapter?.itemCount!! > 0) {
                            recyclerView.smoothScrollToPosition(recyclerView.adapter?.itemCount!! - 1)
                        }
                    }
                }
            })

        chatViewModel.getBackButtonClicked().observe(this, { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        chatViewModel.getShowNoInternet().observe(this, {
            if(it) {
                chatViewModel.setShowNoInternet(false)
                showInfoAlertDialog(this, getString(R.string.no_internet))
            }
        })

        chatViewModel.getBaseResponse().observe(this, {
            it?.let {
                chatViewModel.setBaseResponse(null)
                validateResponse(this, it)
            }
        })
    }

    private fun addListeners() {
        recyclerView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                recyclerView.postDelayed(Runnable {
                    if (recyclerView.adapter != null && recyclerView.adapter?.itemCount != null && recyclerView.adapter?.itemCount!! > 0) {
                        if (shouldMoveToBottom) {
                            recyclerView.smoothScrollToPosition(recyclerView.adapter?.itemCount!! - 1)
                        }
                    }
                }, 100)
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                shouldMoveToBottom = !recyclerView.canScrollVertically(1)
            }
        })
    }
}