package com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderPagerDetailPictureBinding

class DetailPictureFlowPagerViewHolder(
    private val binding: ViewHolderPagerDetailPictureBinding,
    private val clickListener: DetailPictureFlowClickListener,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.imgPicture.setOnClickListener {
            clickListener.onDetailPictureClick()
        }
    }

    fun onBind(detailPictureUrl: String) {
        Glide
            .with(context)
            .load(detailPictureUrl)
            .into(binding.imgPicture)
    }

}