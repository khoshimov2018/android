package ru.behetem.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.behetem.R
import ru.behetem.adapters.ChatMessagesAdapter
import ru.behetem.databinding.ActivityChatBinding
import ru.behetem.models.ChatRoomModel
import ru.behetem.models.UserModel
import ru.behetem.utils.Constants
import ru.behetem.utils.getLoggedInUserFromShared
import ru.behetem.viewmodels.ChatViewModel
import ru.behetem.viewmodels.ChooseLookingForViewModel

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private var chatMessagesAdapter: ChatMessagesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        binding.lifecycleOwner = this
        initViewModel()
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

        chatViewModel.getBackButtonClicked().observe(this, { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })
    }
}