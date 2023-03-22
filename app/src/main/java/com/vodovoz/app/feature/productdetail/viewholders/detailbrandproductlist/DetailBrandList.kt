package com.vodovoz.app.feature.productdetail.viewholders.detailbrandproductlist

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.ProductUI

data class DetailBrandList(
    val id: Int,
    val productUiList: List<ProductUI>
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_product_details_brand_product_list
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailBrandList) return false

        return id == item.id
    }
}
