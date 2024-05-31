package com.vodovoz.app.feature.productdetail.viewholders.detailservices.inner

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.ui.model.ServiceUI.Companion.SERVICE_VIEW_TYPE

class ServicesFlowAdapter(
    val clickListener: ProductDetailsClickListener
): ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            SERVICE_VIEW_TYPE -> {
                ServicesFlowViewHolder(getViewFromInflater(R.layout.view_holder_service, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}