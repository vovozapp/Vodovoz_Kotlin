package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPagerDetailPictureBinding
import com.vodovoz.app.ui.view_holder.DetailPicturePagerViewHolder
import com.vodovoz.app.ui.interfaces.IOnProductDetailPictureClick

class DetailPicturePagerAdapter(
    private val iOnProductDetailPictureClick: IOnProductDetailPictureClick
) : RecyclerView.Adapter<DetailPicturePagerViewHolder>() {

    var detailPictureUrlList =  listOf<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = DetailPicturePagerViewHolder(
        binding = ViewHolderPagerDetailPictureBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        iOnProductDetailPictureClick = iOnProductDetailPictureClick,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: DetailPicturePagerViewHolder,
        position: Int
    ) = holder.onBind(detailPictureUrlList[position])

    override fun getItemCount() = detailPictureUrlList.size

}