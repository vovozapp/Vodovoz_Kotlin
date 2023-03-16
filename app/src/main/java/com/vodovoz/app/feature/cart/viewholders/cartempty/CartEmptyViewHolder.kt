package com.vodovoz.app.feature.cart.viewholders.cartempty

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemCartEmptyBinding
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener

class CartEmptyViewHolder(
    view: View,
    val clickListener: CartMainClickListener
) : ItemViewHolder<CartEmpty>(view) {

    private val binding: ItemCartEmptyBinding = ItemCartEmptyBinding.bind(view)

    init {
        binding.btnGoToCatalog.setOnClickListener {
            clickListener.onGoToCatalogClick()
        }
    }
}