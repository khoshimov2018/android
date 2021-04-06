package ru.behetem.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import ru.behetem.databinding.AdapterChatMessageItemBinding
import ru.behetem.models.ChatMessageModel
import ru.behetem.models.ChatRoomModel
import ru.behetem.viewmodels.ChatViewModel
import ru.behetem.viewmodels.MessengerViewModel

class ChatMessagesAdapter(
    private val chatMessagesList: MutableList<ChatMessageModel>,
    private val viewModel: ChatViewModel
) :
    RecyclerView.Adapter<ChatMessagesAdapter.ChatMessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterChatMessageItemBinding.inflate(layoutInflater, parent, false)
        return ChatMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val currentMessage = chatMessagesList[position]
        holder.binding.item = currentMessage
        holder.binding.viewModel = viewModel
        holder.binding.executePendingBindings()
        holder.binding.content.maxWidth = viewModel.getMaxWidth()
    }

    override fun getItemCount(): Int = chatMessagesList.size

    class ChatMessageViewHolder(val binding: AdapterChatMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun markAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun markDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }
}