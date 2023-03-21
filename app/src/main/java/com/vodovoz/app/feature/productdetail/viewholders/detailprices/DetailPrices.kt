package com.vodovoz.app.feature.productdetail.viewholders.detailprices

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.PriceUI

data class DetailPrices(
    val id: Int,
    val priceUiList: List<PriceUI>
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_product_details_prices
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailPrices) return false

        return id == item.id
    }
}
