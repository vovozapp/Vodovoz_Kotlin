package com.vodovoz.app.ui.fragment.favorite

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.productlist.adapter.SortedAdapter
import com.vodovoz.app.ui.fragment.favorite.FavoriteFlowViewModel

class FavoritesListController(
    private val viewModel: FavoriteFlowViewModel,
    cartManager: CartManager,
    likeManager: LikeManager,
    productsClickListener: ProductsClickListener,
    private val context: Context
) {
    private val favoritesAdapter = SortedAdapter(productsClickListener, cartManager, likeManager)

    fun bind(recyclerView: RecyclerView, refresh: SwipeRefreshLayout) {
        initList(recyclerView)
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
        when (manager) {
            FavoriteFlowViewModel.LINEAR -> {
                imageViewMode.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.png_table))
                changeToLinearLayoutManager(recyclerView)
            }
            FavoriteFlowViewModel.GRID -> {
                imageViewMode.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.png_list))
                changeToGridLayoutManager(recyclerView)
            }
        }
    }

    private fun changeToGridLayoutManager(recyclerView: RecyclerView) {
        (recyclerView.layoutManager as GridLayoutManager).spanCount = 2
    }

    private fun changeToLinearLayoutManager(recyclerView: RecyclerView) {
        (recyclerView.layoutManager as GridLayoutManager).spanCount = 1
    }

    private fun bindRefresh(refresh: SwipeRefreshLayout) {

        refresh.setOnRefreshListener {
            viewModel.refreshSorted()
            refresh.isRefreshing = false
        }
    }
}