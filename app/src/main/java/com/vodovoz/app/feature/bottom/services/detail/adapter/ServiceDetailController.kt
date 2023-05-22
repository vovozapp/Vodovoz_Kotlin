package com.vodovoz.app.feature.bottom.services.detail.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener

class ServiceDetailController(
    cartManager: CartManager,
    likeManager: LikeManager,
    ratingProductManager: RatingProductManager,
    productsClickListener: ProductsClickListener
) {
    private val serviceDetailAdapter = ServiceDetailAdapter(cartManager, likeManager, ratingProductManager, productsClickListener)

    fun bind(recyclerView: RecyclerView) {
        initList(recyclerView)
    }

    fun submitList(list: List<Item>) {
        serviceDetailAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = serviceDetailAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}