package com.vodovoz.app.feature.productdetail.viewholders.detailblocks.inner

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.HolderProductDetailBlockBinding
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.ui.model.BlockUI

class BlockFlowViewHolder(
    view: View,
    val clickListener: ProductDetailsClickListener,
) : ItemViewHolder<BlockUI>(view) {

    private val binding: HolderProductDetailBlockBinding =
        HolderProductDetailBlockBinding.bind(view)

    init {
        binding.buttonBlock.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onBlockButtonClick(
                item.productId,
                item.extProductId
            )
        }

    }

    override fun bind(item: BlockUI) {
        super.bind(item)

        with(binding) {
            textViewBlockOpisanie.text = item.description
            buttonBlock.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor(item.button.background)
            )
            buttonBlock.setTextColor(
                ColorStateList.valueOf(
                    Color.parseColor(item.button.textColor)
                )
            )
            buttonBlock.text = item.button.name
        }
    }
}