package com.vodovoz.app.feature.map.adapter

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.yandex.mapkit.geometry.Point

data class AddressResult(
    val text: String,
    val point: Point
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_address_result
    }

    override fun areItemsTheSame(item: Item): Boolean {
        return this == item
    }

}
