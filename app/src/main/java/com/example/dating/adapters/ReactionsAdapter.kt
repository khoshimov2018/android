package com.example.dating.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.example.dating.databinding.AdapterReactionItemBinding
import com.example.dating.models.ReactionModel

class ReactionsAdapter(private val reactionsList: MutableList<ReactionModel>) :
    RecyclerView.Adapter<ReactionsAdapter.ReactionViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterReactionItemBinding.inflate(layoutInflater, parent, false)
        return ReactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReactionViewHolder, position: Int) {
        val currentReaction = reactionsList[position]
        holder.binding.item = currentReaction
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = reactionsList.size

    class ReactionViewHolder(val binding: AdapterReactionItemBinding) :
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