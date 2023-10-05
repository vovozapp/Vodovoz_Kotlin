package com.vodovoz.app.feature.filters.product.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.FilterUI

class ProductFiltersFlowAdapter(
    private val onFilterClickListener: OnFilterClickListener,
    private val onFilterClearClickListener: OnFilterClearClickListener,
    private val onFilterRangeListener: (FilterUI) -> Unit,
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            R.layout.view_holder_filter ->
                ProductFiltersFlowViewHolder(
                    getViewFromInflater(viewType, parent),
                    onFilterClickListener,
                    onFilterClearClickListener
                )
            R.layout.view_holder_filter_range ->
                ProductFiltersRangeFlowViewHolder(
                    getViewFromInflater(viewType, parent),
                    onFilterClearClickListener,
                    onFilterRangeListener
                )
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}