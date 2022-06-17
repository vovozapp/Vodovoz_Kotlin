package com.vodovoz.app.ui.components.base.picturePagerAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderDetailPictureBinding

class DetailPictureSliderAdapter : RecyclerView.Adapter<DetailPictureSliderViewHolder>() {

    var detailPictureUrlList =  listOf<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = DetailPictureSliderViewHolder(
        binding = ViewHolderSliderDetailPictureBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: DetailPictureSliderViewHolder,
        position: Int
    ) = holder.onBind(detailPictureUrlList[position])

    override fun getItemCount() = detailPictureUrlList.size

}