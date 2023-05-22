package com.vodovoz.app.feature.bottom.services.detail.adapter

import android.view.View
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.ItemServiceDetailBinding
import com.vodovoz.app.feature.bottom.services.detail.model.ServiceDetailBlockUI
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener

class ServiceDetailViewHolder(
    view: View,
    cartManager: CartManager,
    likeManager: LikeManager,
    productsClickListener: ProductsClickListener,
    clickListener: ServiceDetailClickListener,
) : ItemViewHolder<ServiceDetailBlockUI>(view) {

    private val binding: ItemServiceDetailBinding = ItemServiceDetailBinding.bind(view)

    override fun bind(item: ServiceDetailBlockUI) {
        super.bind(item)

        binding.txtBlockTitle.text = item.blockTitle

    }
}