package com.vodovoz.app.feature.onlyproducts

import android.content.Context
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ItemController
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.productlist.adapter.SortedAdapter
import com.vodovoz.app.ui.decoration.ListMarginDecoration

class OnlyProductsController(
    private val viewModel: OnlyProductsViewModel,
    cartManager: CartManager,
    likeManager: LikeManager,
    productsClickListener: ProductsClickListener,
    private val context: Context,
    ratingProductManager: RatingProductManager,
) : ItemController(
    SortedAdapter(
        productsClickListener,
        cartManager,
        likeManager,
        ratingProductManager
    )
) {
    private val space: Int by lazy { context.resources.getDimension(R.dimen.space_16).toInt() }

    private val linearMarginDecoration: ListMarginDecoration by lazy {
        ListMarginDecoration((space * 0.8).toInt())
    }
    private val linearDividerItemDecoration: DividerItemDecoration by lazy {
        DividerItemDecoration(context, VERTICAL)
    }

    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)
        with(recyclerView) {
            layoutManager = GridLayoutManager(context, 1)
            addItemDecoration(linearMarginDecoration)
            addItemDecoration(linearDividerItemDecoration)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = layoutManager as GridLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        viewModel.loadMoreSorted()
                    }
                }
            })
        }
    }

    override fun bindRefresh(refresh: SwipeRefreshLayout) {
        refresh.setOnRefreshListener {
            viewModel.refreshSorted()
            refresh.isRefreshing = false
        }
    }
}