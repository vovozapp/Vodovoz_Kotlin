package com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPagerDetailPictureBinding

class DetailPictureFlowPagerAdapter(
    private val clickListener: DetailPictureFlowClickListener
) : RecyclerView.Adapter<DetailPictureFlowPagerViewHolder>() {

    var detailPictureUrlList =  listOf<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = DetailPictureFlowPagerViewHolder(
        binding = ViewHolderPagerDetailPictureBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        clickListener = clickListener,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: DetailPictureFlowPagerViewHolder,
        position: Int
    ) = holder.onBind(detailPictureUrlList[position])

    override fun getItemCount() = detailPictureUrlList.size

}