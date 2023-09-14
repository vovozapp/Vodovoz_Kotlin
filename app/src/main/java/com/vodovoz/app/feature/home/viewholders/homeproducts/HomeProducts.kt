package com.vodovoz.app.feature.home.viewholders.homeproducts

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductUI

data class HomeProducts(
    val id : Int,
    val items: List<CategoryDetailUI>,
    val productsSliderConfig: ProductsSliderConfig,
    val productsType: Int,
    val prodList: List<ProductUI> = emptyList()
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_slider_product
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomeProducts) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomeProducts) return false

        return this == item
    }

    companion object {
        const val DISCOUNT = 4
        const val TOP_PROD = 6
        const val NOVELTIES = 9
        const val BOTTOM_PROD = 11
        const val VIEWED = 14

        fun fetchHomeProductsByType(
            data: List<CategoryDetailUI>,
            type: Int,
            position: Int,
        ): HomeProducts {
            return HomeProducts(
                position,
                data,
                productsType = type,
                productsSliderConfig = ProductsSliderConfig(
                    containShowAllButton = true
                ),
                prodList = data.first().productUIList
            )
        }
    }
}