package com.vodovoz.app.ui.fragment.home.viewholders.homepopulars.inneradapter

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.CategoryUI

class HomePopularsDiffUtilCallback: DiffUtil.ItemCallback<CategoryUI>() {

    override fun areContentsTheSame(oldItem: CategoryUI, newItem: CategoryUI): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: CategoryUI, newItem: CategoryUI): Boolean {
        return oldItem.id == newItem.id
    }
}