package com.vodovoz.app.feature.productdetail.viewholders.detailcatandbrand

import android.view.View
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener

class DetailCatAndBrandViewHolder(
    view: View,
    val clickListener: ProductDetailsClickListener,
    private val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager,
    private val cartManager: CartManager
) : ItemViewHolder<DetailCatAndBrand>(view) {



}