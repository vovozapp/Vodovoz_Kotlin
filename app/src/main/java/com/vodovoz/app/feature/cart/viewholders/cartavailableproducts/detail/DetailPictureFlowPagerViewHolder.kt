package com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail

import android.os.Parcelable
import android.view.View
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderPagerDetailPictureBinding
import com.vodovoz.app.databinding.ViewHolderSliderDetailPictureBinding
import kotlinx.parcelize.Parcelize

class DetailPictureFlowPagerViewHolder(
    view: View,
    clickListener: DetailPictureFlowClickListener
) : ItemViewHolder<DetailPicturePager>(view) {

    private val binding: ViewHolderPagerDetailPictureBinding = ViewHolderPagerDetailPictureBinding.bind(view)

    init {
        binding.imgPicture.setOnClickListener {
            clickListener.onDetailPictureClick()
        }
    }

    override fun bind(item: DetailPicturePager) {
        super.bind(item)

        Glide
            .with(itemView.context)
            .load(item.url)
            .into(binding.imgPicture)
    }

}

@Parcelize
data class DetailPicturePager(
    val url: String
): Item, Parcelable {
    override fun getItemViewType(): Int {
        return R.layout.view_holder_pager_detail_picture
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailPicturePager) return false

        return this == item
    }

}

class DetailPictureFlowSliderViewHolder(
    view: View,
    clickListener: DetailPictureFlowClickListener
) : ItemViewHolder<DetailPictureSlider>(view) {

    private val binding: ViewHolderSliderDetailPictureBinding = ViewHolderSliderDetailPictureBinding.bind(view)

    init {
        binding.imgPicture.setOnClickListener {
            val item = item ?: return@setOnClickListener

            if (item.isAvailable)
                clickListener.onProductClick(item.id)
        }
    }

    override fun bind(item: DetailPictureSlider) {
        super.bind(item)
        when(item.isAvailable) {
            true -> {
                binding.imgPicture.alpha = 1.0f
            }
            false -> {
                binding.imgPicture.alpha = 0.5f
            }
        }
        Glide
            .with(itemView.context)
            .load(item.url)
            .into(binding.imgPicture)

    }
}

data class DetailPictureSlider(
    val id: Long,
    val url: String,
    val isAvailable: Boolean
): Item {
    override fun getItemViewType(): Int {
        return R.layout.view_holder_slider_detail_picture
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailPictureSlider) return false

        return this.id == item.id
    }

}