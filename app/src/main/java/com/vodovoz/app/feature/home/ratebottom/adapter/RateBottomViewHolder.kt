package com.vodovoz.app.feature.home.ratebottom.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.databinding.ItemRateProductBottomBinding
import com.vodovoz.app.feature.home.ratebottom.model.ProductRateBottom

class RateBottomViewHolder(
    view: View,
    clickListener: RateBottomClickListener
) : ItemViewHolder<ProductRateBottom>(view) {

    private val binding: ItemRateProductBottomBinding = ItemRateProductBottomBinding.bind(view)

    init {
        binding.dontRateBtn.setOnClickListener {
            val itemId = item?.id?.toLong() ?:return@setOnClickListener
            clickListener.dontCommentProduct(itemId)
        }

        binding.ratingBar.setOnRatingBarChangeListener { _, fl, _ ->
            val itemId = item?.id?.toLong() ?:return@setOnRatingBarChangeListener
            clickListener.rateProduct(itemId, fl.toInt())
        }
    }

    override fun bind(item: ProductRateBottom) {
        super.bind(item)

        binding.titleTv.text = item.name

        Glide.with(itemView.context)
            .load(item.image?.parseImagePath())
            .placeholder(R.drawable.placeholderimageproduits)
            .error(R.drawable.placeholderimageproduits)
            .into(binding.productIv)

    }
}