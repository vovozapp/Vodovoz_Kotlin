package com.vodovoz.app.feature.profile.waterapp.adapter

import android.view.ViewGroup
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper

class WaterAppAdapter(
    private val waterAppHelper: WaterAppHelper,
    private val clickListener: WaterAppClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {

            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}