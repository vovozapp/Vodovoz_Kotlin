package com.vodovoz.app.feature.promotiondetail

import android.content.Context
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.productlist.adapter.SortedAdapter
import com.vodovoz.app.ui.decoration.ListMarginDecoration

class PromotionDetailFlowController(
    private val viewModel: PromotionDetailFlowViewModel,
    cartManager: CartManager,
    likeManager: LikeManager,
    productsClickListener: ProductsClickListener,
    private val context: Context
) {
    private val space: Int by lazy { context.resources.getDimension(R.dimen.space_16).toInt() }

    private val productsAdapter = SortedAdapter(productsClickListener, cartManager, likeManager)

    private val linearMarginDecoration: ListMarginDecoration by lazy {
        ListMarginDecoration((space*0.8).toInt())
    }
    private val linearDividerItemDecoration: DividerItemDecoration by lazy {
        DividerItemDecoration(context, VERTICAL)
    }

    fun bind(recyclerView: RecyclerView, refresh: SwipeRefreshLayout?) {
        initList(recyclerView)
        if (refresh == null) return
        bindRefresh(refresh)
    }

    fun submitList(list: List<Item>) {
        productsAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = productsAdapter
            layoutManager = GridLayoutManager(context, 1)
            addItemDecoration(linearMarginDecoration)
            addItemDecoration(linearDividerItemDecoration)
        }
    }

    private fun bindRefresh(refresh: SwipeRefreshLayout) {

        refresh.setOnRefreshListener {
            viewModel.refreshSorted()
            refresh.isRefreshing = false
        }
    }
}