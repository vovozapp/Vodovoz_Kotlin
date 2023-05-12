package com.vodovoz.app.feature.home.ratebottom.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.databinding.ItemCollapsedRateImageBinding
import com.vodovoz.app.feature.home.ratebottom.model.CollapsedDataImage

class RateBottomImageViewHolder(
    view: View
) : ItemViewHolder<CollapsedDataImage>(view) {

    private val binding: ItemCollapsedRateImageBinding = ItemCollapsedRateImageBinding.bind(view)

    override fun bind(item: CollapsedDataImage) {
        super.bind(item)

        Glide.with(itemView.context)
            .load(item.image?.parseImagePath())
            .placeholder(R.drawable.placeholderimageproduits)
            .error(R.drawable.placeholderimageproduits)
            .into(binding.collapsedItemImage)
    }

}