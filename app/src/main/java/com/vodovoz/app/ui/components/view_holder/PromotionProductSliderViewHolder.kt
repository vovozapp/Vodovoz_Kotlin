package com.vodovoz.app.ui.components.view_holder

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderSliderPromotionProductBinding
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setDiscountText
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionProductSliderViewHolder(
    private val binding: ViewHolderSliderPromotionProductBinding,
    private val onProductClickSubject: PublishSubject<Long>,
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>>,
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.oldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.root.setOnClickListener {
            onProductClickSubject.onNext(productUI.id)
        }

        binding.favoriteStatus.setOnClickListener {
            when(productUI.isFavorite) {
                true -> {
                    productUI.isFavorite = false
                    binding.favoriteStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite))
                }
                false -> {
                    productUI.isFavorite = true
                    binding.favoriteStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_red))
                }
            }
            onFavoriteClickSubject.onNext(Pair(productUI.id, productUI.isFavorite))
        }
    }

    private lateinit var productUI: ProductUI

    fun onBind(productUI: ProductUI) {
        this.productUI = productUI

        binding.name.text = productUI.name

        when(productUI.status) {
            "" -> binding.statusContainer.visibility = View.GONE
            else -> {
                binding.statusContainer.visibility = View.VISIBLE
                binding.status.text = productUI.status
                binding.statusContainer.setCardBackgroundColor(Color.parseColor(productUI.statusColor))
            }
        }

        when(productUI.oldPrice) {
            0 -> binding.discountContainer.visibility = View.GONE
            else -> {
                binding.discountContainer.visibility = View.VISIBLE
                binding.discount.visibility = View.VISIBLE
                binding.price.setTextColor(ContextCompat.getColor(context, R.color.red))
                binding.oldPrice.setPriceText(productUI.oldPrice)
                binding.oldPrice.visibility = View.VISIBLE
                binding.discount.setDiscountText(
                    productUI.oldPrice,
                    productUI.newPrice
                )
            }
        }


        if (productUI.oldPrice != 0 && productUI.status != "") binding.space.visibility = View.VISIBLE
        if (productUI.oldPrice != 0 || productUI.status != "") {
            val padding = context.resources.getDimension(R.dimen.primary_margin) / 2
            binding.name.setPadding(0, padding.toInt(), 0, 0)
        }

        binding.price.setPriceText(productUI.newPrice)

        Glide
            .with(context)
            .load(productUI.detailPicture)
            .into(binding.image)

    }

}