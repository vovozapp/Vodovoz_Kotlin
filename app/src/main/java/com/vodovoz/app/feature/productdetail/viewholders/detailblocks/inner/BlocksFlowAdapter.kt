package com.vodovoz.app.feature.productdetail.viewholders.detailblocks.inner

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener

class BlocksFlowAdapter(
    val clickListener: ProductDetailsClickListener,
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when (viewType) {
            R.layout.holder_product_detail_block -> {
                BlockFlowViewHolder(
                    getViewFromInflater(
                        R.layout.holder_product_detail_block,
                        parent
                    ),
                    clickListener
                )
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}