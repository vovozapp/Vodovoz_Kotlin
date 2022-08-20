package com.vodovoz.app.ui.view_holder

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderSliderProductBinding
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setDiscountText
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class ProductSliderViewHolder(
    private val binding: ViewHolderSliderProductBinding,
    private val onProductClickSubject: PublishSubject<Long>,
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>>,
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>>,
    private val context: Context,
    private val cardWidth: Int
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var productUI: ProductUI

    private val amountControllerTimer = object: CountDownTimer(3000, 3000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            hideAmountController()
        }
    }

    init {
        binding.root.layoutParams = RecyclerView.LayoutParams(
            cardWidth,
            (cardWidth * 1.2).toInt()
        )
        binding.oldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.root.setOnClickListener {
            onProductClickSubject.onNext(productUI.id)
        }

        binding.amountController.add.setOnClickListener {
            if (productUI.cartQuantity == 0) {
                productUI.cartQuantity++
                onChangeProductQuantitySubject.onNext(Pair(productUI.id, productUI.cartQuantity))
                updateCartQuantity()
            }
            showAmountController()
        }

        binding.amountController.reduceAmount.setOnClickListener {
            productUI.cartQuantity--
            if (productUI.cartQuantity < 0) productUI.cartQuantity = 0
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            onChangeProductQuantitySubject.onNext(Pair(productUI.id, productUI.cartQuantity))
            updateCartQuantity()
        }

        binding.amountController.increaseAmount.setOnClickListener {
            productUI.cartQuantity++
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            onChangeProductQuantitySubject.onNext(Pair(productUI.id, productUI.cartQuantity))
            updateCartQuantity()
        }

        binding.favoriteStatus.setOnClickListener {
            when(productUI.isFavorite) {
                true -> {
                    productUI.isFavorite = false
                    binding.favoriteStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.png_ic_favorite))
                }
                false -> {
                    productUI.isFavorite = true
                    binding.favoriteStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.png_ic_favorite_red))
                }
            }
            onFavoriteClickSubject.onNext(Pair(productUI.id, productUI.isFavorite))
        }
    }

    private fun updateCartQuantity() {
        if (productUI.cartQuantity < 0) {
            productUI.cartQuantity = 0
        }
        binding.amountController.amount.text = productUI.cartQuantity.toString()
        binding.amountController.circleAmount.text = productUI.cartQuantity.toString()
    }

    private fun showAmountController() {
        binding.amountController.circleAmount.visibility = View.GONE
        binding.amountController.add.visibility = View.GONE
        binding.amountController.amountControllerDeployed.visibility = View.VISIBLE
        binding.price.visibility = View.INVISIBLE
        binding.oldPrice.visibility = View.INVISIBLE
        amountControllerTimer.start()
    }

    private fun hideAmountController() {
        if (productUI.cartQuantity > 0) {
            binding.amountController.circleAmount.visibility = View.VISIBLE
        }
        binding.price.visibility = View.VISIBLE
        binding.oldPrice.visibility = View.VISIBLE
        binding.amountController.add.visibility = View.VISIBLE
        binding.amountController.amountControllerDeployed.visibility = View.GONE
    }

    fun onBind(productUI: ProductUI) {
        this.productUI = productUI

        binding.amountController.circleAmount.text = productUI.cartQuantity.toString()
        binding.amountController.amount.text = productUI.cartQuantity.toString()

        when (productUI.cartQuantity > 0) {
            true -> binding.amountController.circleAmount.visibility = View.VISIBLE
            false -> binding.amountController.circleAmount.visibility = View.GONE
        }

        when(productUI.isFavorite) {
            false -> binding.favoriteStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.png_ic_favorite))
            true -> binding.favoriteStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.png_ic_favorite_red))
        }

        when(productUI.status) {
            "" -> binding.statusContainer.visibility = View.GONE
            else -> {
                binding.statusContainer.visibility = View.VISIBLE
                binding.status.text = productUI.status
                binding.statusCard.setCardBackgroundColor(Color.parseColor(productUI.statusColor))
            }
        }

        when(productUI.priceList.size) {
            1 -> {
                binding.price.setPriceText(productUI.priceList.first().currentPrice)
                when (productUI.priceList.first().oldPrice) {
                    0 -> binding.discountContainer.visibility = View.GONE
                    else -> {
                        binding.discountContainer.visibility = View.VISIBLE
                        binding.discount.visibility = View.VISIBLE
                        binding.price.setTextColor(ContextCompat.getColor(context, R.color.red))
                        binding.oldPrice.setPriceText(productUI.priceList.first().oldPrice)
                        binding.oldPrice.visibility = View.VISIBLE
                        binding.discount.setDiscountText(
                            productUI.priceList.first().oldPrice,
                            productUI.priceList.first().currentPrice
                        )
                        if (productUI.status != "") {
                            binding.spaceBetweenStatuses.visibility = View.VISIBLE
                        } else {
                            binding.spaceBetweenStatuses.visibility = View.GONE
                        }
                    }
                }
            }
            else -> {
                binding.spaceBetweenStatuses.visibility = View.GONE
                binding.discount.visibility = View.GONE
                binding.price.setPriceText(productUI.priceList.sortedBy { it.requiredAmount }.reversed().find { it.requiredAmount <= productUI.cartQuantity }?.currentPrice ?: productUI.priceList.first().currentPrice)
            }
        }

        Glide
            .with(context)
            .load(productUI.detailPicture)
            .into(binding.image)

    }
}