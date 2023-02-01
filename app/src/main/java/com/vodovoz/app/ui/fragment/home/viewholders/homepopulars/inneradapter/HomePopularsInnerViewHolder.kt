package com.vodovoz.app.ui.fragment.home.viewholders.homepopulars.inneradapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderPopularCategoryBinding
import com.vodovoz.app.ui.model.CategoryUI

class HomePopularsInnerViewHolder(
    view: View,
    private val clickListener: HomePopularCategoriesSliderClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: ViewHolderSliderPopularCategoryBinding = ViewHolderSliderPopularCategoryBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onCategoryClick(item.id)
        }
    }

    fun bind(category: CategoryUI) {
        binding.tvName.text = category.name
    }

    private fun getItemByPosition(): CategoryUI? {
        return (bindingAdapter as? HomePopularsInnerAdapter)?.currentList?.get(bindingAdapterPosition)
    }
}