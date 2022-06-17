package com.vodovoz.app.ui.components.base.picturePagerAdapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderDetailPictureBinding

class DetailPictureSliderViewHolder(
    private val binding: ViewHolderSliderDetailPictureBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(detailPictureUrl: String) {
        Glide
            .with(context)
            .load(detailPictureUrl)
            .into(binding.detailPicture)
    }

}