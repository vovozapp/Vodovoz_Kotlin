package com.vodovoz.app.feature.cart

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.cart.adapter.CartMainAdapter
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener

class CartController(
    listener: CartMainClickListener,
    productsClickListener: ProductsClickListener
) {
    private val cartMainAdapter = CartMainAdapter(listener, productsClickListener)

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