package com.vodovoz.app.feature.cart.bottles.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.BottleUI

class AllBottlesFlowAdapter(
    private val clickListener: OnBottleClickListener,
) : ItemAdapter() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItemViewHolder<out Item> {
        return when (viewType) {
            BottleUI.BOTTLE_VIEW_TYPE -> {
                BottleNameFlowViewHolder(
                    getViewFromInflater(
                        R.layout.view_holder_bottle_name,
                        parent
                    ), clickListener
                )
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}