package com.vodovoz.app.feature.cart

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ItemController
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.cart.adapter.CartMainAdapter
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener

class CartController(
    listener: CartMainClickListener,
    productsClickListener: ProductsClickListener,
    cartManager: CartManager,
    likeManager: LikeManager,
    ratingProductManager: RatingProductManager,
) : ItemController(
    CartMainAdapter(
        listener,
        productsClickListener,
        cartManager,
        likeManager,
        ratingProductManager
    )
) {

    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
        }
    }

}