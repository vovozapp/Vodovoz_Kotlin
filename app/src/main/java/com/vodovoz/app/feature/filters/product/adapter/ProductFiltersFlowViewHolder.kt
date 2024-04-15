package com.vodovoz.app.feature.filters.product.adapter

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderFilterBinding
import com.vodovoz.app.ui.extensions.FilterValueTextExtensions.setFilterValue
import com.vodovoz.app.ui.model.FilterUI

class ProductFiltersFlowViewHolder(
    view: View,
    private val onFilterClickListener: OnFilterClickListener,
    private val onFilterClearClickListener: OnFilterClearClickListener
) : ItemViewHolder<FilterUI>(view) {

    private val binding: ViewHolderFilterBinding = ViewHolderFilterBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            onFilterClickListener.onClickFilter(filterUI)
        }
        binding.imgClear.setOnClickListener {
            filterUI.filterValueList.clear()
            fillValue(filterUI)
            onFilterClearClickListener.onFilterClearClick(filterUI)
        }
    }

    private lateinit var filterUI: FilterUI

    override fun bind(item: FilterUI) {
        super.bind(item)
        filterUI = item
        binding.tvName.text = filterUI.name
        fillValue(filterUI)
    }

    private fun fillValue(filterUI: FilterUI) {
        with(binding) {
            when(filterUI.filterValueList.size) {
                0 -> {
                    tvValue.visibility = View.INVISIBLE
                    imgClear.visibility = View.INVISIBLE
                }
                else -> {
                    tvValue.visibility = View.VISIBLE
                    imgClear.visibility = View.VISIBLE
                    tvValue.setFilterValue(filterUI.filterValueList)
                }
            }
        }
    }

}