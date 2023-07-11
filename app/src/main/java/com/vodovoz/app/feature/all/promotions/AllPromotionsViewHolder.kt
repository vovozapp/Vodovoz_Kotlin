package com.vodovoz.app.feature.all.promotions

import android.graphics.Color
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderPromotionBinding
import com.vodovoz.app.feature.all.AllClickListener
import com.vodovoz.app.ui.model.PromotionUI

class AllPromotionsViewHolder(
    view: View,
    private val allClickListener: AllClickListener,
) : ItemViewHolder<PromotionUI>(view) {

    private val binding: ViewHolderPromotionBinding = ViewHolderPromotionBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            allClickListener.onPromotionClick(item.id)
        }

        binding.advIncludeCard.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            allClickListener.onPromotionAdvClick(item.promotionAdvEntity)
        }
    }

    override fun bind(item: PromotionUI) {
        super.bind(item)

        binding.tvName.text = item.name
        binding.tvTimeLeft.text = item.timeLeft
        when(item.customerCategory) {
            null -> binding.tvCustomerCategory.visibility = View.GONE
            else -> binding.tvCustomerCategory.text = item.customerCategory
        }
        item.statusColor?.let { noNullColor ->
            binding.cwCustomerCategory.setCardBackgroundColor(Color.parseColor(noNullColor))
        }

        binding.advIncludeCard.root.isVisible = item.promotionAdvEntity != null
        binding.advIncludeCard.advTv.text = item.promotionAdvEntity?.titleHeader

        Glide
            .with(itemView.context)
            .load(item.detailPicture)
            .into(binding.imgImage)
    }
}