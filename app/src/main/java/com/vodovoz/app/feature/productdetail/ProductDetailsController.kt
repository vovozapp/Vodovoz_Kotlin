package com.vodovoz.app.feature.productdetail

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsAdapter
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener

class ProductDetailsController(
    listener: ProductDetailsClickListener,
    productsClickListener: ProductsClickListener,
    productsShowAllListener: ProductsShowAllListener,
    promotionsClickListener: PromotionsClickListener,
    cartManager: CartManager,
    likeManager: LikeManager,
    val context: Context
) {
    private val productDetailsAdapter = ProductDetailsAdapter(
        clickListener = listener,
        productsClickListener = productsClickListener,
        productsShowAllListener = productsShowAllListener,
        promotionsClickListener = promotionsClickListener,
        cartManager = cartManager,
        likeManager = likeManager
    )

    fun bind(recyclerView: RecyclerView, fab: ConstraintLayout) {
        initList(recyclerView, fab)
    }

    fun submitList(list: List<Item>) {
        productDetailsAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView, fab: ConstraintLayout) {
        with(recyclerView) {
            adapter = productDetailsAdapter
            layoutManager = LinearLayoutManager(context)

            val divider = MaterialDividerItemDecoration(
                context,
                MaterialDividerItemDecoration.VERTICAL
            )
            divider.dividerThickness = 24
            divider.dividerColor = ContextCompat.getColor(context, R.color.light_gray)
            recyclerView.addItemDecoration(divider)

            addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy > 10) {
                        fab.visibility = View.VISIBLE
                    }

                    if (dy < 0) {
                        fab.visibility = View.GONE
                    }
                }
            })
        }
    }

}