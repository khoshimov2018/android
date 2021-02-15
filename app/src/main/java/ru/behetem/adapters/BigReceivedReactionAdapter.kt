package ru.behetem.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import ru.behetem.databinding.AdapterBigReceivedReactionItemBinding
import ru.behetem.databinding.AdapterReactionItemBinding
import ru.behetem.databinding.AdapterReceivedReactionItemBinding
import ru.behetem.interfaces.IReactionClick
import ru.behetem.models.ReactionModel
import ru.behetem.viewmodels.MessengerViewModel
import ru.behetem.viewmodels.UserProfileViewModel

class BigReceivedReactionAdapter(
    private val reactionsList: MutableList<ReactionModel>,
    private val viewModel: IReactionClick
) :
    RecyclerView.Adapter<BigReceivedReactionAdapter.ReceivedReactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceivedReactionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterBigReceivedReactionItemBinding.inflate(layoutInflater, parent, false)
        return ReceivedReactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceivedReactionViewHolder, position: Int) {
        val currentReaction = reactionsList[position]
        holder.binding.item = currentReaction
        holder.binding.viewModel = viewModel
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = reactionsList.size

    class ReceivedReactionViewHolder(val binding: AdapterBigReceivedReactionItemBinding) :
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