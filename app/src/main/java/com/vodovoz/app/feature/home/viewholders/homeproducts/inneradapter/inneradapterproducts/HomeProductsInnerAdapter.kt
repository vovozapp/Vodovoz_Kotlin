package com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.ProductUI.Companion.PRODUCT_VIEW_TYPE

class HomeProductsInnerAdapter(
    private val clickListener: ProductsClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            PRODUCT_VIEW_TYPE -> {
                HomeProductsInnerViewHolder(getViewFromInflater(R.layout.view_holder_slider_product, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}