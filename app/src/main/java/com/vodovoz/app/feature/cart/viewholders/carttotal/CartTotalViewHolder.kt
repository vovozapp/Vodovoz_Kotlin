package com.vodovoz.app.feature.cart.viewholders.carttotal

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemCartTotalBinding
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText

class CartTotalViewHolder(
    view: View,
    val clickListener: CartMainClickListener
) : ItemViewHolder<CartTotal>(view) {

    private val binding: ItemCartTotalBinding = ItemCartTotalBinding.bind(view)

    init {
        binding.tvApplyPromoCode.setOnClickListener {
            val code = binding.etCoupon.text.toString()
            clickListener.onApplyPromoCodeClick(code)
        }
    }

    override fun bind(item: CartTotal) {
        super.bind(item)

        binding.tvFullPrice.setPriceText(item.prices.fullPrice)
        binding.tvDepositPrice.setPriceText(item.prices.deposit)
        binding.tvDiscountPrice.setPriceText(item.prices.discountPrice, true)
        binding.tvTotalPrice.setPriceText(item.prices.total)
    }
}