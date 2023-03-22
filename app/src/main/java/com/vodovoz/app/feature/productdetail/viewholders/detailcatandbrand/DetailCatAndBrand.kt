package com.vodovoz.app.feature.productdetail.viewholders.detailcatandbrand

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.BrandUI
import com.vodovoz.app.ui.model.CategoryUI

data class DetailCatAndBrand(
    val id: Int,
    val category: CategoryUI,
    val brandUI: BrandUI? = null
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_product_details_cat_and_brand
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailCatAndBrand) return false

        return id == item.id
    }
}
