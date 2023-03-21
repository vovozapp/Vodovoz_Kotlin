package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.productdetail.viewholders.detailheader.DetailHeader
import kotlinx.parcelize.Parcelize

@Parcelize
data class PriceUI(
    val currentPrice: Int,
    val oldPrice: Int,
    val requiredAmount: Int
): Parcelable, Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_price
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is PriceUI) return false

        return this == item
    }

}