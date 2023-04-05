package com.vodovoz.app.feature.cart

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
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
    ratingProductManager: RatingProductManager
) {
    private val cartMainAdapter = CartMainAdapter(listener, productsClickListener, cartManager, likeManager, ratingProductManager)

    fun bind(recyclerView: RecyclerView) {
        initList(recyclerView)
    }

    fun submitList(list: List<Item>) {
        cartMainAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = cartMainAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

}