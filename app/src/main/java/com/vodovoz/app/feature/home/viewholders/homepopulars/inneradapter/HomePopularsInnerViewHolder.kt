package com.vodovoz.app.feature.home.viewholders.homepopulars.inneradapter

import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
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
        Glide
            .with(itemView.context)
            .load(item.detailPicture)
            .into(binding.image)
        binding.tvName.text = item.name
    }

    private fun getItemByPosition(): CategoryUI? {
        return (bindingAdapter as? HomePopularsInnerAdapter)?.getItem(bindingAdapterPosition) as? CategoryUI
    }
}