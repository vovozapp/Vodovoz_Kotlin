package com.vodovoz.app.ui.fragment.home.viewholders.homebrands.inneradapter

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.BrandUI

class HomeBrandsInnerDiffUtilCallback: DiffUtil.ItemCallback<BrandUI>() {

    override fun areContentsTheSame(oldItem: BrandUI, newItem: BrandUI): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: BrandUI, newItem: BrandUI): Boolean {
        return oldItem.id == newItem.id
    }
}