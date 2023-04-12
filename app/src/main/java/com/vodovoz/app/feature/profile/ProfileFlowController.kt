package com.vodovoz.app.feature.profile

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.home.adapter.HomeMainAdapter
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener
import com.vodovoz.app.feature.home.viewholders.homeorders.inneradapter.HomeOrdersSliderClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.profile.adapter.ProfileFlowAdapter
import com.vodovoz.app.feature.profile.adapter.ProfileFlowClickListener

class ProfileFlowController(
    private val viewModel: ProfileFlowViewModel,
    cartManager: CartManager,
    likeManager: LikeManager,
    listener: ProfileFlowClickListener,
    productsShowAllListener: ProductsShowAllListener,
    productsClickListener: ProductsClickListener,
    homeOrdersSliderClickListener: HomeOrdersSliderClickListener
) {

    private val profileFlowAdatper = ProfileFlowAdapter(
        listener,
        cartManager,
        likeManager,
        productsShowAllListener,
        productsClickListener,
        homeOrdersSliderClickListener
    )

    fun bind(recyclerView: RecyclerView, refresh: SwipeRefreshLayout) {
        initList(recyclerView)
        bindRefresh(refresh)
    }

    fun submitList(list: List<Item>) {
        profileFlowAdatper.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = profileFlowAdatper
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