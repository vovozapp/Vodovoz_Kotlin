package com.vodovoz.app.ui.components.fragment.productSlider.productAdapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderSliderProductBinding
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setDiscountText
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI

class ProductSliderViewHolder(
    private val binding: ViewHolderSliderProductBinding,
    private val context: Context,
    private val cardWidth: Int
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var productUI: ProductUI

    init {
        binding.root.layoutParams = RecyclerView.LayoutParams(
            cardWidth,
            (cardWidth * 1.2).toInt()
        )
        binding.oldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
    }

    fun onBind(productUI: ProductUI) {
        this.productUI = productUI

        when(productUI.status) {
            "" -> binding.statusContainer.visibility = View.GONE
            else -> {
                binding.statusContainer.visibility = View.VISIBLE
                binding.status.text = productUI.status
                binding.statusCard.setCardBackgroundColor(Color.parseColor(productUI.statusColor))
            }
        }

        when(productUI.oldPrice) {
            0 -> binding.discountContainer.visibility = View.GONE
            else -> {
                binding.discountContainer.visibility = View.VISIBLE
                binding.discount.visibility = View.VISIBLE
                binding.price.setTextColor(ContextCompat.getColor(context, R.color.red))
                binding.oldPrice.setPriceText(productUI.oldPrice)
                binding.discount.setDiscountText(
                    productUI.oldPrice,
                    productUI.newPrice
                )
            }
        }

        binding.price.setPriceText(productUI.newPrice)

        Glide
            .with(context)
            .load(productUI.detailPicture)
            .into(binding.image)

    }
}