package com.vodovoz.app.feature.productdetail.viewholders.detailheader

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductDetailUI

data class DetailHeader(
    val id: Int,
    val productDetailUI: ProductDetailUI,
    val replacementProductsCategoryDetail: CategoryDetailUI? = null,
    val categoryId: Long? = null
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_product_details_header
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailHeader) return false

        return id == item.id
    }
}
