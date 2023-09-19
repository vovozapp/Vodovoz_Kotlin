package com.vodovoz.app.feature.home.viewholders.homepopulars.inneradapter

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderSliderPopularCategoryBinding
import com.vodovoz.app.ui.model.CategoryUI

class HomePopularsInnerViewHolder(
    view: View,
    private val clickListener: HomePopularCategoriesSliderClickListener
) : ItemViewHolder<CategoryUI>(view) {

    private val binding: ViewHolderSliderPopularCategoryBinding = ViewHolderSliderPopularCategoryBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onCategoryClick(item.id)
        }
    }

    override fun bind(item: CategoryUI) {
        super.bind(item)
        binding.tvName.text = item.name
    }

    private fun getItemByPosition(): CategoryUI? {
        return (bindingAdapter as? HomePopularsInnerAdapter)?.getItem(bindingAdapterPosition) as? CategoryUI
    }
}