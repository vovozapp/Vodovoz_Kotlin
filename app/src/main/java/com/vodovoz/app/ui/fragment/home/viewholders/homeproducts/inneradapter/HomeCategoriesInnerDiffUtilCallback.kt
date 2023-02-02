package com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.inneradapter

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.CategoryDetailUI

class HomeCategoriesInnerDiffUtilCallback: DiffUtil.ItemCallback<CategoryDetailUI>() {

    override fun areContentsTheSame(oldItem: CategoryDetailUI, newItem: CategoryDetailUI): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: CategoryDetailUI, newItem: CategoryDetailUI): Boolean {
        return oldItem.id == newItem.id
    }
}