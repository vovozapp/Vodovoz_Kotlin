package com.vodovoz.app.feature.favorite.bestforyouadapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ItemController
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener

class BestForYouController(
    cartManager: CartManager,
    likeManager: LikeManager,
    productsShowAllListener: ProductsShowAllListener,
    productsClickListener: ProductsClickListener,
) : ItemController(
    BestForYouAdapter(
        cartManager,
        likeManager,
        productsClickListener,
        productsShowAllListener
    )
) {

    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
        }
    }
}