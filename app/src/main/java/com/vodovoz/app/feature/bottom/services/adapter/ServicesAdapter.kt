package com.vodovoz.app.feature.bottom.services.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.ServiceUI

class ServicesAdapter(
    private val clickListener: ServicesClickListener
): ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            ServiceUI.SERVICE_VIEW_TYPE -> {
                ServicesViewHolder(getViewFromInflater(R.layout.view_holder_service_detail, parent), clickListener)
            }
            R.layout.view_holder_service_detail_new -> {
                ServicesNewViewHolder(getViewFromInflater(R.layout.view_holder_service_detail_new, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}