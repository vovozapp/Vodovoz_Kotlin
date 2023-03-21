package com.vodovoz.app.feature.home

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.home.adapter.HomeMainAdapter
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener

class HomeController(
    private val viewModel: HomeFlowViewModel,
    cartManager: CartManager,
    likeManager: LikeManager,
    listener: HomeMainClickListener,
    productsShowAllListener: ProductsShowAllListener,
    productsClickListener: ProductsClickListener,
    promotionsClickListener: PromotionsClickListener
) {
    private val homeMainAdapter = HomeMainAdapter(listener, cartManager, likeManager, productsShowAllListener, productsClickListener, promotionsClickListener)

    fun bind(recyclerView: RecyclerView, refresh: SwipeRefreshLayout) {
        initList(recyclerView)
        bindRefresh(refresh)
    }

    fun submitList(list: List<Item>) {
        homeMainAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = homeMainAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun bindRefresh(refresh: SwipeRefreshLayout) {

        refresh.setOnRefreshListener {
            viewModel.refresh()
            refresh.isRefreshing = false
        }
    }
}