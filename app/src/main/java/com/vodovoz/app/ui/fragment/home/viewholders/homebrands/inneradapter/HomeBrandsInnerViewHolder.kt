package com.vodovoz.app.ui.fragment.home.viewholders.homebrands.inneradapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderBrandBinding
import com.vodovoz.app.ui.model.BrandUI

class HomeBrandsInnerViewHolder(
    view: View,
    private val clickListener: HomeBrandsSliderClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: ViewHolderSliderBrandBinding = ViewHolderSliderBrandBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onBrandClick(item.id)
        }
    }

    fun bind(banner: BrandUI) {
        Glide
            .with(itemView.context)
            .load(banner.detailPicture)
            .into(binding.imgPicture)
    }

    private fun getItemByPosition(): BrandUI? {
        return (bindingAdapter as? HomeBrandsInnerAdapter)?.currentList?.get(bindingAdapterPosition)
    }
}