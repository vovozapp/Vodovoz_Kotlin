package com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.inneradapter

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.CategoryDetailUI

class HomeCategoriesInnerDiffUtilCallback(
    private val oldItems: List<CategoryDetailUI>,
    private val newItems: List<CategoryDetailUI>
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