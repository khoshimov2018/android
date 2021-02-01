package ru.behetem.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import ru.behetem.databinding.AdapterInterestItemBinding
import ru.behetem.interfaces.IInterestClick
import ru.behetem.models.InterestModel

class InterestsAdapter(
    private val interestsList: MutableList<InterestModel>,
    private val iInterestClick: IInterestClick
) :
    RecyclerView.Adapter<InterestsAdapter.InterestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterInterestItemBinding.inflate(layoutInflater, parent, false)
        return InterestViewHolder(binding)
    }

    override fun getItemCount(): Int = interestsList.size

    override fun onBindViewHolder(holder: InterestViewHolder, position: Int) {
        val currentInterest = interestsList[position]
        holder.binding.item = currentInterest
        holder.binding.iInterestClick = iInterestClick
        holder.binding.executePendingBindings()
    }

    override fun onViewAttachedToWindow(holder: InterestViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    class InterestViewHolder(val binding: AdapterInterestItemBinding) :
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