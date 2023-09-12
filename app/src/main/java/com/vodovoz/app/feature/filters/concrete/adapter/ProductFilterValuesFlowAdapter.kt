package com.vodovoz.app.feature.filters.concrete.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder

class ProductFilterValuesFlowAdapter() : ItemAdapter() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItemViewHolder<out Item> {
        return when (viewType) {
            R.layout.view_holder_brand_filter_value -> {
                ProductFilterValueFlowViewHolder(
                    getViewFromInflater(
                        R.layout.view_holder_filter_value,
                        parent
                    )
                )
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}