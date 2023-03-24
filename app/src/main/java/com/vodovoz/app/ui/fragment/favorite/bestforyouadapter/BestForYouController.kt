package com.vodovoz.app.ui.fragment.favorite.bestforyouadapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener

class BestForYouController(
    cartManager: CartManager,
    likeManager: LikeManager,
    productsShowAllListener: ProductsShowAllListener,
    productsClickListener: ProductsClickListener
) {
    private val bestForYouAdapter = BestForYouAdapter(cartManager, likeManager, productsClickListener, productsShowAllListener)

    fun bind(recyclerView: RecyclerView) {
        initList(recyclerView)
    }

    fun submitList(list: List<Item>) {
        bestForYouAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = bestForYouAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}