package com.example.dating.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.example.dating.databinding.AdapterNationalityItemBinding
import com.example.dating.models.NationalityModel
import com.example.dating.viewmodels.NationalitiesViewModel

class NationalitiesAdapter(
    private val nationalitiesList: MutableList<NationalityModel>,
    private val nationalitiesViewModel: NationalitiesViewModel
) :
    RecyclerView.Adapter<NationalitiesAdapter.NationalityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NationalityViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterNationalityItemBinding.inflate(layoutInflater, parent, false)
        return NationalityViewHolder(binding)
    }

    override fun getItemCount(): Int = nationalitiesList.size

    override fun onBindViewHolder(holder: NationalityViewHolder, position: Int) {
        val currentNationality = nationalitiesList[position]
        holder.binding.item = currentNationality
        holder.binding.viewModel = nationalitiesViewModel
        holder.binding.executePendingBindings()
    }

    override fun onViewAttachedToWindow(holder: NationalityViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    class NationalityViewHolder(val binding: AdapterNationalityItemBinding) :
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