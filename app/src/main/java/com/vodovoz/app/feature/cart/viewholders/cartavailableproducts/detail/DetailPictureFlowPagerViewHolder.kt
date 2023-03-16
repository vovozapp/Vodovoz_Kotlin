package com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderPagerDetailPictureBinding
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.ui.interfaces.IOnProductDetailPictureClick

class DetailPictureFlowPagerViewHolder(
    private val binding: ViewHolderPagerDetailPictureBinding,
    private val clickListener: DetailPictureFlowClickListener,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.imgPicture.setOnClickListener {
            clickListener.onProductClick()
        }
    }

    fun onBind(detailPictureUrl: String) {
        Glide
            .with(context)
            .load(detailPictureUrl)
            .into(binding.imgPicture)
    }

}