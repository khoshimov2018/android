package ru.behetem.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import ru.behetem.databinding.AdapterChatRoomItemBinding
import ru.behetem.models.ChatRoomModel
import ru.behetem.viewmodels.MessengerViewModel

class ChatRoomsAdapter(
    private val chatRoomsList: MutableList<ChatRoomModel>,
    private val viewModel: MessengerViewModel
) :
    RecyclerView.Adapter<ChatRoomsAdapter.ReceivedReactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceivedReactionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterChatRoomItemBinding.inflate(layoutInflater, parent, false)
        return ReceivedReactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceivedReactionViewHolder, position: Int) {
        val currentChatRoom = chatRoomsList[position]
        holder.binding.item = currentChatRoom
        holder.binding.viewModel = viewModel
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = chatRoomsList.size

    class ReceivedReactionViewHolder(val binding: AdapterChatRoomItemBinding) :
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