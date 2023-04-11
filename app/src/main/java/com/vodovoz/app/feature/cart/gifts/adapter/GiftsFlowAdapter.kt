package com.vodovoz.app.feature.cart.gifts.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressViewHolder
import com.vodovoz.app.ui.model.ProductUI

class GiftsFlowAdapter(
    private val clickListener: GiftsFlowClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            ProductUI.PRODUCT_VIEW_TYPE -> {
                GiftsFlowViewHolder(getViewFromInflater(R.layout.view_holder_promotion, parent), clickListener)
            }
            ProductUI.PRODUCT_VIEW_TYPE_GRID -> {
                GiftsFlowViewHolder(getViewFromInflater(R.layout.view_holder_promotion, parent), clickListener)
            }
            R.layout.item_progress -> {
                BottomProgressViewHolder(getViewFromInflater(viewType, parent))
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}