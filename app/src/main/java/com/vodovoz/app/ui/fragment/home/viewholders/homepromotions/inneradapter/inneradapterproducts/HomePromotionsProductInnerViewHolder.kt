package com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.inneradapter.inneradapterproducts

import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderSliderPromotionProductBinding
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPricePerUnitText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI

class HomePromotionsProductInnerViewHolder(
    view: View,
    private val clickListener: HomePromotionsProductInnerClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: ViewHolderSliderPromotionProductBinding = ViewHolderSliderPromotionProductBinding.bind(view)

    private val amountControllerTimer = object: CountDownTimer(3000, 3000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            hideAmountController()
        }
    }

    init {
        binding.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onPromotionProductClick(item.id)
        }

        binding.amountController.add.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            if (item.leftItems == 0) {
                clickListener.onNotifyWhenBeAvailable(item.id, item.name, item.detailPicture)
                return@setOnClickListener
            }
            if (item.cartQuantity == 0) {
                item.cartQuantity++
                clickListener.onChangeProductQuantity(item.id, item.cartQuantity)
                updateCartQuantity()
            }
            showAmountController()
        }

        binding.amountController.reduceAmount.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            item.cartQuantity--
            if (item.cartQuantity < 0) item.cartQuantity = 0
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            clickListener.onChangeProductQuantity(item.id, item.cartQuantity)
            updateCartQuantity()
        }

        binding.amountController.increaseAmount.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            item.cartQuantity++
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            clickListener.onChangeProductQuantity(item.id, item.cartQuantity)
            updateCartQuantity()
        }

        binding.imgFavoriteStatus.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            when(item.isFavorite) {
                true -> {
                    item.isFavorite = false
                    binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_favorite_black))
                }
                false -> {
                    item.isFavorite = true
                    binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_ic_favorite_red))
                }
            }
            clickListener.onFavoriteClick(item.id, item.isFavorite)
        }
    }

    fun bind(productUI: ProductUI) {
        binding.tvName.text = productUI.name

        //If left items = 0
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
                binding.tvPricePerUnit.visibility = View.VISIBLE
                binding.tvPricePerUnit.setPricePerUnitText(productUI.pricePerUnit)
            }
            false -> binding.tvPricePerUnit.visibility = View.GONE
        }

        //Price
        var haveDiscount = false
        when(productUI.priceList.size) {
            1 -> {
                binding.tvCurrentPrice.setPriceText(productUI.priceList.first().currentPrice)
                binding.tvOldPrice.setPriceText(productUI.priceList.first().oldPrice)
                if (productUI.priceList.first().currentPrice < productUI.priceList.first().oldPrice || productUI.isGift) haveDiscount = true
            }
            else -> {
                val minimalPrice = productUI.priceList.maxByOrNull { it.requiredAmount }!!
                binding.tvCurrentPrice.setMinimalPriceText(minimalPrice.currentPrice)
                binding.tvPricePerUnit.visibility = View.GONE
            }
        }
        when(haveDiscount) {
            true -> {
                binding.tvCurrentPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                binding.tvOldPrice.visibility = View.VISIBLE
            }
            false -> {
                binding.tvCurrentPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_black))
                binding.tvOldPrice.visibility = View.GONE
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
            true -> binding.cwStatusContainer.visibility = View.GONE
            false -> {
                binding.cwStatusContainer.visibility = View.VISIBLE
                binding.tvStatus.text = productUI.status
                binding.cwStatusContainer.setCardBackgroundColor(Color.parseColor(productUI.statusColor))
            }
        }

        //DiscountPercent
        when(productUI.priceList.size == 1 &&
                productUI.priceList.first().currentPrice < productUI.priceList.first().oldPrice) {
            true -> {
                isNotHaveStatuses = false
                binding.cwDiscountContainer.visibility = View.VISIBLE
                binding.tvDiscountPercent.setDiscountPercent(
                    newPrice = productUI.priceList.first().currentPrice,
                    oldPrice = productUI.priceList.first().oldPrice
                )
            }
            false -> binding.cwDiscountContainer.visibility = View.GONE
        }

        when(isNotHaveStatuses) {
            true -> binding.cgStatuses.visibility = View.GONE
            false -> binding.cgStatuses.visibility = View.VISIBLE
        }

        //UpdatePictures
        Glide
            .with(itemView.context)
            .load(productUI.detailPicture)
            .into(binding.imgPicture)
    }

    private fun getItemByPosition(): ProductUI? {
        return (bindingAdapter as? HomePromotionsProductInnerAdapter)?.getItem(bindingAdapterPosition)
    }

    private fun hideAmountController() {
        val product = getItemByPosition()
        product?.let {
            if (product.cartQuantity > 0) {
                binding.amountController.circleAmount.visibility = View.VISIBLE
            }
            binding.amountController.add.visibility = View.VISIBLE
            binding.amountController.amountControllerDeployed.visibility = View.INVISIBLE
        }
    }

    private fun updateCartQuantity() {
        val product = getItemByPosition()
        product?.let {
            if (product.cartQuantity < 0) {
                product.cartQuantity = 0
            }
            binding.amountController.amount.text = product.cartQuantity.toString()
            binding.amountController.circleAmount.text = product.cartQuantity.toString()
        }
    }

    private fun showAmountController() {
        binding.amountController.circleAmount.visibility = View.INVISIBLE
        binding.amountController.add.visibility = View.INVISIBLE
        binding.amountController.amountControllerDeployed.visibility = View.VISIBLE
        amountControllerTimer.start()
    }
}