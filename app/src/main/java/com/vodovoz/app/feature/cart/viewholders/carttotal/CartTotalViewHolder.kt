package com.vodovoz.app.feature.cart.viewholders.carttotal

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
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
            val imm =
                ContextCompat.getSystemService(binding.root.context, InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(binding.etCoupon.windowToken, 0)
            binding.etCoupon.clearFocus()
        }
    }

    override fun bind(item: CartTotal) {
        super.bind(item)

        binding.etCoupon.setText(item.coupon)

        binding.tvFullPrice.setPriceText(item.prices.fullPrice)
        binding.tvDepositPrice.setPriceText(item.prices.deposit)
        binding.tvDiscountPrice.setPriceText(item.prices.discountPrice, true)
        binding.tvTotalPrice.setPriceText(item.prices.total)
    }
}