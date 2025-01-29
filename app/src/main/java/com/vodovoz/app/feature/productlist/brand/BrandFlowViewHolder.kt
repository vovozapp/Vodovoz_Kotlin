package com.vodovoz.app.feature.productlist.brand

import android.view.View
import androidx.core.content.ContextCompat
import com.tbuonomo.viewpagerdotsindicator.setBackgroundCompat
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderBrandFilterValueBinding
import com.vodovoz.app.ui.model.FilterValueUI

class BrandFlowViewHolder(
    view: View,
    clickListener: BrandFlowClickListener
) : ItemViewHolder<FilterValueUI>(view = view) {

    private val binding: ViewHolderBrandFilterValueBinding = ViewHolderBrandFilterValueBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val itemId = item ?: return@setOnClickListener
            clickListener.onBrandClick(itemId)
        }
    }

    override fun bind(item: FilterValueUI) {
        super.bind(item)

        binding.tvName.text = item.value

        when(item.isSelected) {
            true -> {
                binding.tvName.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                binding.root.setBackgroundCompat(ContextCompat.getDrawable(itemView.context, R.drawable.bg_tab_selected))
            }
            false -> {
                binding.tvName.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_black_composable))
                binding.root.setBackgroundCompat(ContextCompat.getDrawable(itemView.context, R.drawable.bg_tab_unselected))
            }
        }
    }
}