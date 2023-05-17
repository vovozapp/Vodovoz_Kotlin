package com.vodovoz.app.feature.filters.concrete.adapter

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderFilterValueBinding
import com.vodovoz.app.ui.model.FilterValueUI

class ProductFilterValueViewHolder(
    private val binding: ViewHolderFilterValueBinding
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { changeSelectedState() }
        binding.cbChecked.setOnClickListener { changeSelectedState() }
    }

    private fun changeSelectedState() {
        filterValueUI.isSelected = !filterValueUI.isSelected
        updateSelectedState()
    }

    private lateinit var filterValueUI: FilterValueUI

    fun onBind(filterValueUI: FilterValueUI) {
        this.filterValueUI = filterValueUI

        binding.tvName.text = filterValueUI.value
        updateSelectedState()
    }

    private fun updateSelectedState() {
        binding.cbChecked.isChecked = filterValueUI.isSelected
    }

}