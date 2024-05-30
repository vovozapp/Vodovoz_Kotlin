package com.vodovoz.app.feature.replacement

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ItemController
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner.AvailableProductsAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.view.Divider

class ReplacementController(
    cartManager: CartManager,
    likeManager: LikeManager,
    productsClickListener: ProductsClickListener,
    private val context: Context,
    ratingProductManager: RatingProductManager
) : ItemController(AvailableProductsAdapter(productsClickListener, likeManager, cartManager, ratingProductManager)) {

    private val space: Int by lazy { context.resources.getDimension(R.dimen.space_16).toInt() }

    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)

        with(recyclerView) {

            layoutManager = LinearLayoutManager(context)

            ContextCompat.getDrawable(context, R.drawable.bkg_gray_divider)?.let {
                addItemDecoration(Divider(it, space / 2))
            }

            val lastItemBottomSpace = resources.getDimension(R.dimen.last_item_bottom_normal_space).toInt()

            addMarginDecoration {  rect, view, parent, state ->
                rect.top = space / 2
                rect.right = space / 2
                rect.bottom =
                    if (parent.getChildAdapterPosition(view) == state.itemCount - 1) lastItemBottomSpace
                    else space
            }
        }
    }
}