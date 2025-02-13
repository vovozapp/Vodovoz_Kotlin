package com.vodovoz.app.feature.pastpurchases

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
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.productlist.adapter.SortedAdapter
import com.vodovoz.app.ui.decoration.GridMarginDecoration
import com.vodovoz.app.ui.decoration.ListMarginDecoration

class PastPurchasesListController(
    private val viewModel: PastPurchasesFlowViewModel,
    cartManager: CartManager,
    likeManager: LikeManager,
    productsClickListener: ProductsClickListener,
    private val context: Context,
    ratingProductManager: RatingProductManager
) {
    private val space: Int by lazy { context.resources.getDimension(R.dimen.space_16).toInt() }

    private val favoritesAdapter = SortedAdapter(productsClickListener, cartManager, likeManager, ratingProductManager)

    private val gridMarginDecoration: GridMarginDecoration by lazy {
        GridMarginDecoration(space)
    }

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
        favoritesAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = favoritesAdapter
            layoutManager = GridLayoutManager(context, 1)

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

    fun changeLayoutManager(manager: String, recyclerView: RecyclerView, imageViewMode: AppCompatImageView) {
        clearDecorators(recyclerView)
        when (manager) {
            PastPurchasesFlowViewModel.LINEAR -> {
                imageViewMode.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_orientation))
                changeToLinearLayoutManager(recyclerView)
            }
            PastPurchasesFlowViewModel.GRID -> {
                imageViewMode.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_orientation_horizontal))
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
                PastPurchasesFlowViewModel.GRID -> {
                    removeItemDecoration(linearMarginDecoration)
                    removeItemDecoration(linearDividerItemDecoration)
                    addItemDecoration(gridMarginDecoration)
                }
                PastPurchasesFlowViewModel.LINEAR -> {
                    removeItemDecoration(gridMarginDecoration)
                    addItemDecoration(linearMarginDecoration)
                    addItemDecoration(linearDividerItemDecoration)
                }
            }
        }
    }

    private fun bindRefresh(refresh: SwipeRefreshLayout) {

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