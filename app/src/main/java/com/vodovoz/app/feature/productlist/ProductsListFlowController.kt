package com.vodovoz.app.feature.productlist

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
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
import com.vodovoz.app.ui.decoration.GridMarginDecoration
import com.vodovoz.app.ui.decoration.ListMarginDecoration

class ProductsListFlowController(
    private val viewModel: ProductsListFlowViewModel,
    cartManager: CartManager,
    likeManager: LikeManager,
    productsClickListener: ProductsClickListener,
    private val context: Context,
    ratingProductManager: RatingProductManager
) : ItemController(SortedAdapter(productsClickListener, cartManager, likeManager, ratingProductManager)) {

    private val space: Int = 16

    private val gridMarginDecoration: GridMarginDecoration by lazy {
        GridMarginDecoration(space)
    }

    private val linearMarginDecoration: ListMarginDecoration by lazy {
        ListMarginDecoration(space)
    }

    private val linearDividerItemDecoration: DividerItemDecoration by lazy {
        DividerItemDecoration(context, VERTICAL)
    }

    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)
        with(recyclerView) {
            layoutManager = GridLayoutManager(context, 1)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = layoutManager as GridLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount-10) {
                        viewModel.loadMoreSorted()
                    }
                }
            })
        }
    }

    fun changeLayoutManager(manager: String, recyclerView: RecyclerView, imageViewMode: AppCompatImageView) {
        clearDecorators(recyclerView)
        when (manager) {
            ProductsListFlowViewModel.LINEAR -> {
                imageViewMode.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_orientation_horizontal))
                changeToLinearLayoutManager(recyclerView)
            }
            ProductsListFlowViewModel.GRID -> {
                imageViewMode.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_orientation))
                changeToGridLayoutManager(recyclerView)
            }
        }
        updateDecorationsByManager(recyclerView, manager)
    }

    private fun changeToGridLayoutManager(recyclerView: RecyclerView) {
        (recyclerView.layoutManager as GridLayoutManager).spanCount = 2
    }

    private fun changeToLinearLayoutManager(recyclerView: RecyclerView) {
        (recyclerView.layoutManager as GridLayoutManager).spanCount = 1
    }

    private fun updateDecorationsByManager(recyclerView: RecyclerView, manager: String) {
        with(recyclerView) {
            when (manager) {
                ProductsListFlowViewModel.GRID -> {
                    removeItemDecoration(linearMarginDecoration)
                    addItemDecoration(gridMarginDecoration)
                }
                ProductsListFlowViewModel.LINEAR -> {
                    removeItemDecoration(gridMarginDecoration)
                    addItemDecoration(linearMarginDecoration)
                }
            }
        }
    }

    override fun bindRefresh(refresh: SwipeRefreshLayout) {
        refresh.setOnRefreshListener {
            viewModel.refreshSorted()
            refresh.isRefreshing = false
        }
    }

    private fun clearDecorators(recyclerView: RecyclerView) {
        with(recyclerView) {
            removeItemDecoration(linearMarginDecoration)
            removeItemDecoration(linearDividerItemDecoration)
            removeItemDecoration(gridMarginDecoration)
        }
    }
}