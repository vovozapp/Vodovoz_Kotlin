package com.vodovoz.app.feature.home.viewholders.homeproductstabs.inneradapter

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderSliderPopularCategoryBinding
import com.vodovoz.app.feature.home.viewholders.homeproductstabs.HomeTabsClickListener
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.CategoryUI

class HomeTabsViewHolder(
    view: View,
    private val clickListener: HomeTabsClickListener
) : ItemViewHolder<CategoryDetailUI>(view) {

    private val binding: ViewHolderSliderPopularCategoryBinding = ViewHolderSliderPopularCategoryBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onCategoryClick(item.id)
        }
    }

    override fun bind(item: CategoryDetailUI) {
        super.bind(item)
        binding.tvName.text = item.name
    }

}