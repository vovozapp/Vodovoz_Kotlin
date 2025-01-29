package com.vodovoz.app.feature.profile

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ItemController
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.home.viewholders.homeorders.inneradapter.HomeOrdersSliderClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.profile.adapter.ProfileFlowAdapter
import com.vodovoz.app.feature.profile.adapter.ProfileFlowClickListener

class ProfileFlowController(
    private val viewModel: ProfileFlowViewModel,
    cartManager: CartManager,
    likeManager: LikeManager,
    ratingProductManager: RatingProductManager,
    listener: ProfileFlowClickListener,
    productsShowAllListener: ProductsShowAllListener,
    productsClickListener: ProductsClickListener,
    homeOrdersSliderClickListener: HomeOrdersSliderClickListener,
    repeatOrderClickListener: (Long) -> Unit,
    onRecyclerReady: (Boolean) -> Unit,
) : ItemController(
    ProfileFlowAdapter(
        listener,
        cartManager,
        likeManager,
        ratingProductManager,
        productsShowAllListener,
        productsClickListener,
        homeOrdersSliderClickListener,
        repeatOrderClickListener,
        onRecyclerReady,
    )
) {
    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun bindRefresh(refresh: SwipeRefreshLayout) {
        refresh.setOnRefreshListener {
            viewModel.refresh()
            refresh.isRefreshing = false
        }
    }
}