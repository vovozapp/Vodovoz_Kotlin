package com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.inneradapter.inneradapterproducts

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.ProductUI

class HomeProductsInnerDiffUtilCallback: DiffUtil.ItemCallback<ProductUI>() {

    override fun areContentsTheSame(oldItem: ProductUI, newItem: ProductUI): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: ProductUI, newItem: ProductUI): Boolean {
        return oldItem.id == newItem.id
    }
}