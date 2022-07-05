package com.vodovoz.app.ui.components.base.picturePagerAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderDetailPictureBinding
import com.vodovoz.app.ui.components.interfaces.IOnProductClick
import com.vodovoz.app.ui.components.interfaces.IOnProductDetailPictureClick

class DetailPictureSliderAdapter(
    private val iOnProductDetailPictureClick: IOnProductDetailPictureClick
) : RecyclerView.Adapter<DetailPictureSliderViewHolder>() {

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
        iOnProductDetailPictureClick = iOnProductDetailPictureClick,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: DetailPictureSliderViewHolder,
        position: Int
    ) = holder.onBind(detailPictureUrlList[position])

    override fun getItemCount() = detailPictureUrlList.size

}