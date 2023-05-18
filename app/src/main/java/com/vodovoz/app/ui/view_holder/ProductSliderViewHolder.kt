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
import com.vodovoz.app.core.network.ApiConfig.AMOUNT_CONTROLLER_TIMER
import com.vodovoz.app.databinding.ViewHolderSliderProductBinding
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPricePerUnitText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class ProductSliderViewHolder(
    private val binding: ViewHolderSliderProductBinding,
    private val onProductClickSubject: PublishSubject<Long>,
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>>,
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>>,
    private val onNotifyWhenBeAvailable: (Long, String, String) -> Unit,
    private val onNotAvailableMore: () -> Unit,
    private val context: Context,
    private val cardWidth: Int
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var productUI: ProductUI

    private val amountControllerTimer = object: CountDownTimer(AMOUNT_CONTROLLER_TIMER, AMOUNT_CONTROLLER_TIMER) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            hideAmountController()
        }
    }

    init {
        /*binding.llPricesContainer.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.root.setOnClickListener {
            onProductClickSubject.onNext(productUI.id)
        }

        binding.amountController.add.setOnClickListener {
            if (productUI.leftItems == 0) {
                onNotifyWhenBeAvailable(productUI.id, productUI.name, productUI.detailPicture)
                return@setOnClickListener
            }
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
        }*/

        binding.imgFavoriteStatus.setOnClickListener {
            when(productUI.isFavorite) {
                true -> {
                    productUI.isFavorite = false
                    binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_black))
                }
                false -> {
                    productUI.isFavorite = true
                    binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.png_ic_favorite_red))
                }
            }
            onFavoriteClickSubject.onNext(Pair(productUI.id, productUI.isFavorite))
        }
    }

    private fun updateCartQuantity() {
        if (productUI.cartQuantity < 0) {
            productUI.cartQuantity = 0
        }
       /* binding.amountController.amount.text = productUI.cartQuantity.toString()
        binding.amountController.circleAmount.text = productUI.cartQuantity.toString()*/
    }

    private fun showAmountController() {
        /*binding.amountController.circleAmount.visibility = View.GONE
        binding.amountController.add.visibility = View.GONE
        binding.amountController.amountControllerDeployed.visibility = View.VISIBLE
        binding.llPricesContainer.root.visibility = View.INVISIBLE
        amountControllerTimer.start()*/
    }

    private fun hideAmountController() {
       /* if (productUI.cartQuantity > 0) {
            binding.amountController.circleAmount.visibility = View.VISIBLE
        }
        binding.llPricesContainer.root.visibility = View.VISIBLE
        binding.amountController.add.visibility = View.VISIBLE
        binding.amountController.amountControllerDeployed.visibility = View.GONE*/
    }

    fun onBind(productUI: ProductUI) {
        this.productUI = productUI

       /* //If left items = 0
        when(productUI.leftItems == 0) {
            true -> {
                binding.amountController.add.setBackgroundResource(R.drawable.bkg_button_gray_circle_normal)
                binding.amountController.add.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_alert))
            }
            false -> {
                binding.amountController.add.setBackgroundResource(R.drawable.bkg_button_green_circle_normal)
                binding.amountController.add.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_cart))
            }
        }

        //Price per unit / or order quantity
        when(productUI.pricePerUnit != 0) {
            true -> {
                binding.llPricesContainer.tvPricePerUnit.visibility = View.VISIBLE
                binding.llPricesContainer.tvPricePerUnit.setPricePerUnitText(productUI.pricePerUnit)
            }
            false -> binding.llPricesContainer.tvPricePerUnit.visibility = View.GONE
        }

        //Price
        var haveDiscount = false
        when(productUI.priceList.size) {
            1 -> {
                binding.llPricesContainer.tvCurrentPrice.setPriceText(productUI.priceList.first().currentPrice)
                binding.llPricesContainer.tvOldPrice.setPriceText(productUI.priceList.first().oldPrice)
                if (productUI.priceList.first().currentPrice < productUI.priceList.first().oldPrice || productUI.isGift) haveDiscount = true
            }
            else -> {
                val minimalPrice = productUI.priceList.maxByOrNull { it.requiredAmount }!!
                binding.llPricesContainer.tvCurrentPrice.setMinimalPriceText(minimalPrice.currentPrice)
                binding.llPricesContainer.tvPricePerUnit.visibility = View.GONE
            }
        }
        when(haveDiscount) {
            true -> {
                binding.llPricesContainer.tvCurrentPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                binding.llPricesContainer.tvOldPrice.visibility = View.VISIBLE
            }
            false -> {
                binding.llPricesContainer.tvCurrentPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_black))
                binding.llPricesContainer.tvOldPrice.visibility = View.GONE
            }
        }

        //Cart amount
        binding.amountController.circleAmount.text = productUI.cartQuantity.toString()
        binding.amountController.amount.text = productUI.cartQuantity.toString()

        when (productUI.cartQuantity > 0) {
            true -> binding.amountController.circleAmount.visibility = View.VISIBLE
            false -> binding.amountController.circleAmount.visibility = View.GONE
        }

        //Favorite
        when(productUI.isFavorite) {
            false -> binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_favorite_black))
            true -> binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_ic_favorite_red))
        }

        //Status
        var isNotHaveStatuses = true
        when (productUI.status.isEmpty()) {
            true -> binding.cgStatuses.cwStatusContainer.visibility = View.GONE
            false -> {
                isNotHaveStatuses = false
                binding.cgStatuses.cwStatusContainer.visibility = View.VISIBLE
                binding.cgStatuses.tvStatus.text = productUI.status
                binding.cgStatuses.cwStatusContainer.setCardBackgroundColor(Color.parseColor(productUI.statusColor))
            }
        }

        //DiscountPercent
        when(productUI.priceList.size == 1 &&
                productUI.priceList.first().currentPrice < productUI.priceList.first().oldPrice) {
            true -> {
                isNotHaveStatuses = false
                binding.cgStatuses.cwDiscountContainer.visibility = View.VISIBLE
                binding.cgStatuses.tvDiscountPercent.setDiscountPercent(
                    newPrice = productUI.priceList.first().currentPrice,
                    oldPrice = productUI.priceList.first().oldPrice
                )
            }
            false -> binding.cgStatuses.cwDiscountContainer.visibility = View.GONE
        }

        when(isNotHaveStatuses) {
            true -> binding.cgStatuses.root.visibility = View.GONE
            false -> binding.cgStatuses.root.visibility = View.VISIBLE
        }

        //UpdatePictures
        Glide
            .with(context)
            .load(productUI.detailPicture)
            .into(binding.imgPicture)
*/
    }
}