package com.vodovoz.app.feature.productdetail.viewholders.detailheader

import android.view.View
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.feature.productdetail.viewholders.detailcomments.DetailComments

class DetailHeaderViewHolder(
    view: View,
    val clickListener: ProductDetailsClickListener,
    private val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager,
    private val cartManager: CartManager
) : ItemViewHolder<DetailHeader>(view) {



}