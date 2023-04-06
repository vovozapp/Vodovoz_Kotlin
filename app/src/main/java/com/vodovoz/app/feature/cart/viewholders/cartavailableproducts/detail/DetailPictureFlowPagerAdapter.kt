package com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder

class DetailPictureFlowPagerAdapter(
    private val clickListener: DetailPictureFlowClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            R.layout.view_holder_pager_detail_picture -> {
                DetailPictureFlowPagerViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.view_holder_slider_detail_picture -> {
                DetailPictureFlowSliderViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}