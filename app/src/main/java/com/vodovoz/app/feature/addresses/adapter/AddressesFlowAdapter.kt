package com.vodovoz.app.feature.addresses.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.addresses.adapter.viewholders.AddressFlowViewHolder
import com.vodovoz.app.feature.addresses.adapter.viewholders.AddressTitleFlowViewHolder

class AddressesFlowAdapter(
    private val clickListener: AddressesClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            R.layout.view_holder_address -> {
                AddressFlowViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.view_holder_addresses_type_title -> {
                AddressTitleFlowViewHolder(getViewFromInflater(viewType, parent))
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}