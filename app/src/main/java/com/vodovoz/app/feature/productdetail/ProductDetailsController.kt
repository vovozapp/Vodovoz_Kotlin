package com.vodovoz.app.feature.productdetail

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.cart.adapter.CartMainAdapter
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsAdapter
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener

class ProductDetailsController(
    listener: ProductDetailsClickListener,
    productsClickListener: ProductsClickListener,
    productsShowAllListener: ProductsShowAllListener,
    promotionsClickListener: PromotionsClickListener,
    cartManager: CartManager,
    likeManager: LikeManager
) {
    private val productDetailsAdapter = ProductDetailsAdapter(
        clickListener = listener,
        productsClickListener = productsClickListener,
        productsShowAllListener = productsShowAllListener,
        promotionsClickListener = promotionsClickListener,
        cartManager = cartManager,
        likeManager = likeManager
    )

    fun bind(recyclerView: RecyclerView) {
        initList(recyclerView)
    }

    fun submitList(list: List<Item>) {
        productDetailsAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = productDetailsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

}