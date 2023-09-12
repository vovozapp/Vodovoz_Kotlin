package com.vodovoz.app.feature.filters.concrete.adapter

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderFilterValueBinding
import com.vodovoz.app.ui.model.FilterValueUI

class ProductFilterValueFlowViewHolder(
    view: View
) : ItemViewHolder<FilterValueUI>(view) {

    private val binding = ViewHolderFilterValueBinding.bind(view)

    init {
        binding.root.setOnClickListener { changeSelectedState() }
        binding.cbChecked.setOnClickListener { changeSelectedState() }
    }

    private fun changeSelectedState() {
        filterValueUI.isSelected = !filterValueUI.isSelected
        updateSelectedState()
    }

    private lateinit var filterValueUI: FilterValueUI

    override fun bind(item: FilterValueUI) {
        super.bind(item)
        filterValueUI = item

        binding.tvName.text = filterValueUI.value
        updateSelectedState()
    }

    private fun updateSelectedState() {
        binding.cbChecked.isChecked = filterValueUI.isSelected
    }

}