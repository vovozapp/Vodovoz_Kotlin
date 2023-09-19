package com.vodovoz.app.feature.cart.viewholders.cartnotavailableproducts

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.ItemCartNotAvailableProductsBinding
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.cart.viewholders.cartnotavailableproducts.inner.NotAvailableProductsAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.view.Divider

class CartNotAvailableProductsViewHolder(
    view: View,
    val clickListener: CartMainClickListener,
    private val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager
) : ItemViewHolder<CartNotAvailableProducts>(view) {

    private val binding: ItemCartNotAvailableProductsBinding = ItemCartNotAvailableProductsBinding.bind(view)

    private val space: Int by lazy { itemView.context.resources.getDimension(R.dimen.space_16).toInt() }

    private val productsAdapter = NotAvailableProductsAdapter(clickListener, productsClickListener, likeManager)

    init {
        binding.rvNotAvailableProductRecycler.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)

        binding.rvNotAvailableProductRecycler.adapter = productsAdapter

        ContextCompat.getDrawable(itemView.context, R.drawable.bkg_gray_divider)?.let {
            binding.rvNotAvailableProductRecycler.addItemDecoration(Divider(it, space/4))
        }
        binding.rvNotAvailableProductRecycler.addMarginDecoration { rect, _, _, _ ->
            rect.top = space/4
            rect.bottom = space/4
            rect.right = space
            rect.left = space/2
        }
    }

    override fun bind(item: CartNotAvailableProducts) {
        super.bind(item)

        when(item.giftMessage) {
            null -> binding.tvGiftMessage.visibility = View.GONE
            else -> {
                binding.tvGiftMessage.visibility = View.VISIBLE
                binding.tvGiftMessage.text = item.giftMessage.message
            }
        }

        productsAdapter.submitList(item.items)
    }
}