package com.vodovoz.app.feature.productdetail

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.cart.adapter.CartMainAdapter
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
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
    fragment: Fragment
) {
    private val productDetailsAdapter = ProductDetailsAdapter(
        clickListener = listener,
        productsClickListener = productsClickListener,
        productsShowAllListener = productsShowAllListener,
        promotionsClickListener = promotionsClickListener,
        cartManager = cartManager,
        likeManager = likeManager,
        fragment = fragment
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
            addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy > 300) {
                        fab.visibility = View.VISIBLE
                    } else {
                        fab.visibility = View.INVISIBLE
                    }
                }
            })
        }
    }

}