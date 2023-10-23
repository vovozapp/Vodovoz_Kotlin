package com.vodovoz.app.feature.productdetail

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ItemController
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsAdapter
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener


class ProductDetailsController(
    listener: ProductDetailsClickListener,
    productsClickListener: ProductsClickListener,
    productsShowAllListener: ProductsShowAllListener,
    promotionsClickListener: PromotionsClickListener,
    cartManager: CartManager,
    likeManager: LikeManager,
    val context: Context,
    ratingProductManager: RatingProductManager,
) : ItemController(
    ProductDetailsAdapter(
        clickListener = listener,
        productsClickListener = productsClickListener,
        productsShowAllListener = productsShowAllListener,
        promotionsClickListener = promotionsClickListener,
        cartManager = cartManager,
        likeManager = likeManager,
        ratingProductManager = ratingProductManager
    )
) {

    override fun initList(recyclerView: RecyclerView, fab: ConstraintLayout) {
        super.initList(recyclerView, fab)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val pastVisibleItems: Int =
                        (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    fab.visibility = if (pastVisibleItems == 0) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                }
            })
        }
    }

}