package com.vodovoz.app.feature.map.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder

class AddressResultFlowAdapter(
    private val addressResultClickListener: AddressResultClickListener
) : ItemAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when (viewType) {
            R.layout.view_holder_address_result -> {
                AddressResultFlowViewHolder(getViewFromInflater(viewType, parent), addressResultClickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}