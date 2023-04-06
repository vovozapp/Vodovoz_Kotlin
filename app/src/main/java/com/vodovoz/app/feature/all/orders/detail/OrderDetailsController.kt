package com.vodovoz.app.feature.all.orders.detail

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner.AvailableProductsAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.view.Divider

class OrderDetailsController(
    cartManager: CartManager,
    likeManager: LikeManager,
    productsClickListener: ProductsClickListener,
    private val context: Context,
    ratingProductManager: RatingProductManager
) {
    private val space: Int by lazy { context.resources.getDimension(R.dimen.space_16).toInt() }

    private val productsAdapter = AvailableProductsAdapter(
        productsClickListener,
        likeManager,
        cartManager,
        ratingProductManager
    )

    fun bind(recyclerView: RecyclerView) {
        initList(recyclerView)
    }

    fun submitList(list: List<Item>) {
        productsAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = productsAdapter
            layoutManager = LinearLayoutManager(context)

            ContextCompat.getDrawable(context, R.drawable.bkg_gray_divider)?.let {
                addItemDecoration(Divider(it, space))
            }

            addItemDecoration(
                object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        with(outRect) {
                            top = space
                            bottom = space
                            right = space
                        }
                    }
                }
            )
        }
    }
}