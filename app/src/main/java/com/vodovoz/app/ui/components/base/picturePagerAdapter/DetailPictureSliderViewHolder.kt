package com.vodovoz.app.ui.components.base.picturePagerAdapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderDetailPictureBinding
import com.vodovoz.app.ui.components.interfaces.IOnProductClick
import com.vodovoz.app.ui.components.interfaces.IOnProductDetailPictureClick

class DetailPictureSliderViewHolder(
    private val binding: ViewHolderSliderDetailPictureBinding,
    private val iOnProductDetailPictureClick: IOnProductDetailPictureClick,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.detailPicture.setOnClickListener { iOnProductDetailPictureClick.onProductDetailPictureClick() }
    }

    fun onBind(detailPictureUrl: String) {
        Glide
            .with(context)
            .load(detailPictureUrl)
            .into(binding.detailPicture)
    }

}