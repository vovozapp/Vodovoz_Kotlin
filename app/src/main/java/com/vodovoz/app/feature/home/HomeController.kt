package com.vodovoz.app.feature.home

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ItemController
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.home.adapter.HomeMainAdapter
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener
import com.vodovoz.app.feature.home.viewholders.homebanners.BottomBannerManager
import com.vodovoz.app.feature.home.viewholders.homebanners.TopBannerManager
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homeproductstabs.HomeTabsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitleClickListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener

class HomeController(
    private val viewModel: HomeFlowViewModel,
    cartManager: CartManager,
    likeManager: LikeManager,
    listener: HomeMainClickListener,
    productsShowAllListener: ProductsShowAllListener,
    productsClickListener: ProductsClickListener,
    promotionsClickListener: PromotionsClickListener,
    homeTitleClickListener: HomeTitleClickListener,
    homeTabsClickListener: HomeTabsClickListener,
    topBannerManager: TopBannerManager,
    bottomBannerManager: BottomBannerManager,
    private val showRateBottomSheetFragment: () -> Unit,
) : ItemController(
    HomeMainAdapter(
        clickListener = listener,
        cartManager = cartManager,
        likeManager = likeManager,
        productsShowAllListener = productsShowAllListener,
        productsClickListener = productsClickListener,
        promotionsClickListener = promotionsClickListener,
        homeTitleClickListener = homeTitleClickListener,
        homeTabsClickListener = homeTabsClickListener,
        topBannerManager = topBannerManager,
        bottomBannerManager = bottomBannerManager
    )
) {

    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        showRateBottomSheetFragment.invoke()
                    }
                }
            })
        }
    }

    override fun bindRefresh(refresh: SwipeRefreshLayout) {
        refresh.setOnRefreshListener {
            viewModel.refresh()
            refresh.isRefreshing = false
        }
    }
}