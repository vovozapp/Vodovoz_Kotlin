package com.vodovoz.app.feature.cart.viewholders.cartavailableproducts

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.ItemCartAvailableProductsBinding
import com.vodovoz.app.feature.cart.adapter.CartMainAdapter
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner.AvailableProductsAdapter
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.view.Divider

class CartAvailableProductsViewHolder(
    view: View,
    val clickListener: CartMainClickListener,
    productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager,
    private val cartManager: CartManager
) : ItemViewHolder<CartAvailableProducts>(view) {

    private val binding: ItemCartAvailableProductsBinding = ItemCartAvailableProductsBinding.bind(view)

    private val space: Int by lazy { itemView.context.resources.getDimension(R.dimen.space_16).toInt() }

    private val productsAdapter = AvailableProductsAdapter(productsClickListener, likeManager, cartManager)

    init {
        binding.rvAvailableProductRecycler.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)

        binding.rvAvailableProductRecycler.adapter = productsAdapter

        ContextCompat.getDrawable(itemView.context, R.drawable.bkg_gray_divider)?.let {
            binding.rvAvailableProductRecycler.addItemDecoration(Divider(it, space))
        }
        binding.cbReturnBottles.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> binding.rlChooseBottleBtnContainer.visibility = View.VISIBLE
                false -> binding.rlChooseBottleBtnContainer.visibility = View.GONE
            }
        }
        binding.btnChooseBottle.setOnClickListener {
            clickListener.onChooseBtnClick()
        }
        binding.rvAvailableProductRecycler.addMarginDecoration { rect, _, _, _ ->
            rect.top = space
            rect.bottom = space
            rect.right = space
            rect.left = space/2
        }
    }

    override fun bind(item: CartAvailableProducts) {
        super.bind(item)

        binding.rlChooseBottleBtnContainer.visibility = View.GONE

        when(item.showCheckForm || binding.cbReturnBottles.isChecked) {
            true -> binding.llReturnBottlesContainer.visibility = View.VISIBLE
            false -> binding.llReturnBottlesContainer.visibility = View.GONE
        }

        productsAdapter.submitList(item.items)

    }
}