package com.vodovoz.app.ui.components.adapter.brandSliderAdapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderBrandBinding
import com.vodovoz.app.ui.model.BrandUI

class BrandSliderViewHolder(
    private val binding: ViewHolderSliderBrandBinding,
    private val context: Context,
    private val cardWidth: Int
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.layoutParams = RecyclerView.LayoutParams(
            cardWidth,
            (cardWidth * 0.55).toInt()
        )
    }

    private lateinit var brandUI: BrandUI

    fun onBind(brandUI: BrandUI) {
        this.brandUI = brandUI

        Glide
            .with(context)
            .load(brandUI.detailPicture)
            .into(binding.detailPicture)

    }

}