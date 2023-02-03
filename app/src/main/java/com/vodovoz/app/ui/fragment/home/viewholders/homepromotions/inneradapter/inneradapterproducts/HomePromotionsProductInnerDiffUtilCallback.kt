package com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.inneradapter.inneradapterproducts

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.PromotionUI

class HomePromotionsProductInnerDiffUtilCallback(
    private val oldItems: List<ProductUI>,
    private val newItems: List<ProductUI>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].id == newItems[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }
}