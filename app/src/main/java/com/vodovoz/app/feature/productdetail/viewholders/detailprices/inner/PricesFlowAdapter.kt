package com.vodovoz.app.feature.productdetail.viewholders.detailprices.inner

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder

class PricesFlowAdapter: ItemAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            R.layout.view_holder_price -> {
                PricesFlowViewHolder(getViewFromInflater(viewType, parent))
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}