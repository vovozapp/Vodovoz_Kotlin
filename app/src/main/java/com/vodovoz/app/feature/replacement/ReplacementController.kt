package com.vodovoz.app.feature.replacement

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner.AvailableProductsAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.view.Divider

class ReplacementController(
    cartManager: CartManager,
    likeManager: LikeManager,
    productsClickListener: ProductsClickListener,
    private val context: Context
) {
    private val space: Int by lazy { context.resources.getDimension(R.dimen.space_16).toInt() }

    private val productsAdapter = AvailableProductsAdapter(productsClickListener, likeManager, cartManager)

    fun bind(recyclerView: RecyclerView, list: List<Item>) {
        initList(recyclerView, list)
    }

    fun submitList(list: List<Item>) {
        productsAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView, list: List<Item>) {
        with(recyclerView) {
            adapter = productsAdapter
            layoutManager = LinearLayoutManager(context)

            productsAdapter.submitList(list)

            ContextCompat.getDrawable(context, R.drawable.bkg_gray_divider)?.let {
                addItemDecoration(Divider(it, space))
            }

            val lastItemBottomSpace = resources.getDimension(R.dimen.last_item_bottom_normal_space).toInt()

            addMarginDecoration {  rect, view, parent, state ->
                rect.top = space
                rect.right = space
                rect.bottom =
                    if (parent.getChildAdapterPosition(view) == state.itemCount - 1) lastItemBottomSpace
                    else space
            }
        }
    }
}