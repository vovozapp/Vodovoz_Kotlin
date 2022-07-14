package com.vodovoz.app.ui.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderFilterValueBinding
import com.vodovoz.app.ui.model.FilterValueUI

class ProductFilterValueViewHolder(
    private val binding: ViewHolderFilterValueBinding
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { changeSelectedState() }
        binding.check.setOnClickListener { changeSelectedState() }
    }

    private fun changeSelectedState() {
        filterValueUI.isSelected = !filterValueUI.isSelected
        updateSelectedState()
    }

    private lateinit var filterValueUI: FilterValueUI

    fun onBind(filterValueUI: FilterValueUI) {
        this.filterValueUI = filterValueUI

        binding.name.text = filterValueUI.value
        updateSelectedState()
    }

    private fun updateSelectedState() {
        binding.check.isChecked = filterValueUI.isSelected
    }

}