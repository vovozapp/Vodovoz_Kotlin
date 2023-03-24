package com.vodovoz.app.ui.fragment.favorite.categorytabsdadapter

import android.view.View
import androidx.core.content.ContextCompat
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderBrandFilterValueBinding
import com.vodovoz.app.ui.model.CategoryUI

class CategoryTabsFlowViewHolder(
    view: View,
    clickListener: CategoryTabsFlowClickListener
) : ItemViewHolder<CategoryUI>(view = view) {

    private val binding: ViewHolderBrandFilterValueBinding = ViewHolderBrandFilterValueBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val itemId = item?.id ?: return@setOnClickListener
            clickListener.onTabClick(itemId)
        }
    }

    override fun bind(item: CategoryUI) {
        super.bind(item)

        binding.tvName.text = item.name

        when(item.isSelected) {
            true -> binding.tvName.setTextColor(ContextCompat.getColor(itemView.context, R.color.bluePrimary))
            false -> binding.tvName.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_gray))
        }
    }
}