package com.vodovoz.app.feature.cart.viewholders.cartavailableproducts

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.ItemCartAvailableProductsBinding
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner.AvailableProductsAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.view.Divider
import com.vodovoz.app.util.SwipeToRemoveCallback
import com.vodovoz.app.util.extensions.debugLog

class CartAvailableProductsViewHolder(
    view: View,
    val clickListener: CartMainClickListener,
    private val productsClickListener: ProductsClickListener,
    likeManager: LikeManager,
    cartManager: CartManager,
    ratingProductManager: RatingProductManager
) : ItemViewHolder<CartAvailableProducts>(view) {

    private val binding: ItemCartAvailableProductsBinding = ItemCartAvailableProductsBinding.bind(view)

    private val space: Int by lazy { itemView.context.resources.getDimension(R.dimen.space_8).toInt() }

    private val productsAdapter = AvailableProductsAdapter(productsClickListener, likeManager, cartManager, ratingProductManager)

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

        bindSwipeToRemove(binding.rvAvailableProductRecycler)
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

    private fun bindSwipeToRemove(recyclerView: RecyclerView) {
        ItemTouchHelper(object : SwipeToRemoveCallback(itemView.context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                super.onSwiped(viewHolder, i)
                val item = productsAdapter.getItem(viewHolder.bindingAdapterPosition) as? ProductUI ?: return
                if (item.chipsBan == 5 || item.chipsBan == 4) {
                    clickListener.showSnack("Товар невозможно удалить из корзины.")
                } else {
                    showDeleteProductDialog(item.id, item.cartQuantity)
                }
            }
        }).attachToRecyclerView(recyclerView)
    }

    private fun showDeleteProductDialog(productId: Long, oldQ: Int) {
        MaterialAlertDialogBuilder(itemView.context)
            .setMessage("Вы уверены, что хотите удалить?")
            .setNegativeButton("НЕТ") { dialog, which ->
                productsAdapter.notifyDataSetChanged()
                dialog.cancel()
            }
            .setPositiveButton("ДА") { dialog, which ->
                productsClickListener.onChangeProductQuantity(productId, 0, oldQ)
                dialog.cancel()
            }
            .show()
    }
}