package ru.behetem.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_add_photos.*
import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.*
import ru.behetem.R
import ru.behetem.adapters.ChatMessagesAdapter
import ru.behetem.databinding.ActivityChatBinding
import ru.behetem.models.ChatRoomModel
import ru.behetem.models.UserModel
import ru.behetem.utils.*
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
        connectWS()
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
            if (it != null) {
                if (chatMessagesAdapter == null) {
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

        chatViewModel.getOpenImagePicker().observe(this, Observer {
            if (it) {
                chatViewModel.setOpenImagePicker(false)
                openImagePicker()
            }
        })

        chatViewModel.getBackButtonClicked().observe(this, { isPressed: Boolean ->
            if (isPressed) {
                this.onBackPressed()
            }
        })

        chatViewModel.getShowNoInternet().observe(this, {
            if (it) {
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

    private fun connectWS() {
        val request: Request = Request.Builder().url(ApiConstants.WEB_SOCKET_URL).build()
        val client: OkHttpClient = OkHttpClient()
        val ws: WebSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                printLog("Socket onOpen")
                webSocket.send("JSON String here")
                webSocket.close(1000, "Goodbye !")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                printLog("Socket onMessage " + text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                printLog("Socket onClosing")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                printLog("Socket onFailure")
            }
        })
        client.dispatcher.executorService.shutdown()

        ws.close(1000, "Bye")
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

                fileUri?.let {
                    chatViewModel.setImageUri(it)
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
}