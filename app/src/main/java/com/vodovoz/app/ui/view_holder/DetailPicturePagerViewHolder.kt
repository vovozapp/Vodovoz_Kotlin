package com.vodovoz.app.ui.view_holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderPagerDetailPictureBinding
import com.vodovoz.app.ui.interfaces.IOnProductDetailPictureClick

class DetailPicturePagerViewHolder(
    private val binding: ViewHolderPagerDetailPictureBinding,
    private val iOnProductDetailPictureClick: IOnProductDetailPictureClick,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.imgPicture.setOnClickListener { iOnProductDetailPictureClick.onProductDetailPictureClick() }
    }

    fun onBind(detailPictureUrl: String) {
        Glide
            .with(context)
            .load(detailPictureUrl)
            .into(binding.imgPicture)
    }

}