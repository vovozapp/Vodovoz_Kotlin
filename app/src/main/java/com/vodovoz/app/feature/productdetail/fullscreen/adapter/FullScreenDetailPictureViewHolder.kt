package com.vodovoz.app.feature.productdetail.fullscreen.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderFullScreenDetailPictureBinding

class FullScreenDetailPictureViewHolder(
    private val binding: ViewHolderFullScreenDetailPictureBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(detailPictureUrl: String) {
        Glide.with(context)
            .load(detailPictureUrl)
            .into(binding.zvPicture)
    }
}