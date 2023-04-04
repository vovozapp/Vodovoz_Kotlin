package com.vodovoz.app.ui.view_holder

import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.core.network.ApiConfig.AMOUNT_CONTROLLER_TIMER
import com.vodovoz.app.databinding.ViewHolderProductListBinding
import com.vodovoz.app.ui.adapter.DetailPicturePagerAdapter
import com.vodovoz.app.ui.diffUtils.DetailPictureDiffUtilCallback
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDepositPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setOrderQuantity
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceCondition
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPricePerUnitText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.LogSettings

class ProductListViewHolder(
    private val binding: ViewHolderProductListBinding,
    private val isCartItem: Boolean = false,
    private val onProductClick: (Long) -> Unit,
    private val onChangeCartQuantity: (Long, Int) -> Unit,
    private val onChangeFavoriteStatus: (Long, Boolean) -> Unit,
    private val onNotifyWhenBeAvailable: (Long, String, String) -> Unit,
    private val onNotAvailableMore: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val detailPicturePagerAdapter = DetailPicturePagerAdapter(
        iOnProductDetailPictureClick = { onProductClick(productUI.id) }
    )

    private val amountControllerTimer = object: CountDownTimer(AMOUNT_CONTROLLER_TIMER, AMOUNT_CONTROLLER_TIMER) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            onChangeCartQuantity(productUI.id, productUI.cartQuantity)
            hideAmountController()
        }
    }

    init {
        binding.root.setOnClickListener { onProductClick(productUI.id) }
        binding.vpPictures.setOnClickListener { onProductClick(productUI.id) }
        binding.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.vpPictures.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpPictures.adapter = detailPicturePagerAdapter

        TabLayoutMediator(binding.tlIndicators, binding.vpPictures) { _, _ -> }.attach()

        binding.amountController.add.setOnClickListener {
            if (productUI.isGift) return@setOnClickListener
            if (!isCartItem) {
                if (productUI.leftItems == 0) {
                    onNotifyWhenBeAvailable(productUI.id, productUI.name, productUI.detailPicture)
                    return@setOnClickListener
                }
            }
            /*if (productUI.cartQuantity == 0) {
                productUI.cartQuantity++
                updateCartQuantity()
            }*/

            productUI.cartQuantity++
            updateCartQuantity()
            showAmountController()
        }

        binding.amountController.reduceAmount.setOnClickListener {
            productUI.cartQuantity--
            if (productUI.cartQuantity < 0) productUI.cartQuantity = 0
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity()
        }

        binding.amountController.increaseAmount.setOnClickListener {
            productUI.cartQuantity++
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity()
        }

        binding.amountController.reduceAmount.setOnClickListener {
            productUI.cartQuantity--
            if (productUI.cartQuantity < 0) productUI.cartQuantity = 0
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity()
        }

        binding.amountController.increaseAmount.setOnClickListener {
            productUI.cartQuantity++
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity()
        }

        binding.imgFavoriteStatus.setOnClickListener {
            when(productUI.isFavorite) {
                true -> {
                    productUI.isFavorite = false
                    binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_favorite_black))
                }
                false -> {
                    productUI.isFavorite = true
                    binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_ic_favorite_red))
                }
            }
            onChangeFavoriteStatus(productUI.id, productUI.isFavorite)
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
        binding.amountController.circleAmount.visibility = View.INVISIBLE
        binding.amountController.add.visibility = View.INVISIBLE
        binding.amountController.amountControllerDeployed.visibility = View.VISIBLE
        amountControllerTimer.start()
    }

    private fun hideAmountController() {
        if (productUI.cartQuantity > 0) {
            binding.amountController.circleAmount.visibility = View.VISIBLE
        }
        binding.amountController.add.visibility = View.VISIBLE
        binding.amountController.amountControllerDeployed.visibility = View.INVISIBLE
    }

    private lateinit var productUI: ProductUI

    fun onBind(productUI: ProductUI) {
        this.productUI = productUI

        binding.imgFavoriteStatus.visibility = View.VISIBLE
        binding.cgStatuses.visibility = View.VISIBLE
        binding.clPricesContainer.visibility = View.VISIBLE
        binding.tvOldPrice.visibility = View.VISIBLE
        binding.rlAmountControllerContainer.visibility = View.VISIBLE

        binding.tvName.text = productUI.name
        binding.rbRating.rating = productUI.rating.toFloat()

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
        if (productUI.pricePerUnit != 0) {
            binding.tvPricePerUnit.visibility = View.VISIBLE
            binding.tvPricePerUnit.setPricePerUnitText(productUI.pricePerUnit)
        } else if (productUI.orderQuantity != 0) {
            Log.d(LogSettings.DEVELOP_LOG, "${productUI.name} = ${productUI.orderQuantity}")
            binding.tvPricePerUnit.visibility = View.VISIBLE
            binding.tvPricePerUnit.setOrderQuantity(productUI.orderQuantity)
        } else {
            binding.tvPricePerUnit.visibility = View.GONE
        }

        //Price
        var haveDiscount = false
        when(productUI.priceList.size) {
            1 -> {
                binding.tvPrice.setPriceText(productUI.priceList.first().currentPrice, itCanBeGift = true)
                binding.tvOldPrice.setPriceText(productUI.priceList.first().oldPrice)
                binding.tvPriceCondition.visibility = View.GONE
                if (productUI.priceList.first().currentPrice < productUI.priceList.first().oldPrice || productUI.isGift) haveDiscount = true
            }
            else -> {
                when(isCartItem) {
                    true -> {
                        val minimalPrice = productUI.priceList.sortedBy { it.requiredAmount }.find { it.requiredAmount >= productUI.cartQuantity }
                        minimalPrice?.let {
                            binding.tvPrice.setPriceText(minimalPrice.currentPrice)
                            binding.tvPriceCondition.visibility = View.GONE
                        }
                    }
                    false -> {
                        val minimalPrice = productUI.priceList.maxByOrNull { it.requiredAmount }!!
                        binding.tvPrice.setMinimalPriceText(minimalPrice.currentPrice)
                        binding.tvPriceCondition.setPriceCondition(minimalPrice.requiredAmount)
                        binding.tvPriceCondition.visibility = View.VISIBLE

                    }
                }
                binding.tvPricePerUnit.visibility = View.GONE
            }
        }
        when(haveDiscount) {
            true -> {
                binding.tvPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                binding.tvOldPrice.visibility = View.VISIBLE
            }
            false -> {
                binding.tvPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_new_black))
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

        //If cart
        when(isCartItem) {
            true -> binding.llRatingContainer.visibility = View.GONE
            false -> binding.llRatingContainer.visibility = View.VISIBLE
        }

        //Comment
        when (productUI.commentAmount.isEmpty()) {
            true -> binding.tvCommentAmount.visibility = View.GONE
            else -> {
                binding.tvCommentAmount.visibility = View.VISIBLE
                binding.tvCommentAmount.text = productUI.commentAmount
            }
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
                isNotHaveStatuses = false
                binding.cwStatusContainer.visibility = View.VISIBLE
                binding.tvStatus.text = productUI.status
                binding.cwStatusContainer.setCardBackgroundColor(Color.parseColor(productUI.statusColor))
            }
        }

        //If have deposit
        when(productUI.depositPrice != 0) {
            true -> {
                binding.tvDepositPrice.visibility = View.VISIBLE
                binding.tvDepositPrice.setDepositPriceText(productUI.depositPrice)
            }
            false -> binding.tvDepositPrice.visibility = View.GONE
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
        binding.tlIndicators.isVisible = productUI.detailPictureList.size != 1

        val diffUtil = DetailPictureDiffUtilCallback(
            oldList = detailPicturePagerAdapter.detailPictureUrlList,
            newList = productUI.detailPictureList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            detailPicturePagerAdapter.detailPictureUrlList = productUI.detailPictureList
            diffResult.dispatchUpdatesTo(detailPicturePagerAdapter)
        }

        //If is bottle
        if (productUI.isBottle) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.cgStatuses.visibility = View.GONE
            binding.clPricesContainer.visibility = View.GONE
        }

        //If is gift
        if (productUI.isGift) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.cgStatuses.visibility = View.GONE
            if (productUI.isGift) binding.tvOldPrice.visibility = View.GONE
        }

        //If is not available
        if (!productUI.isAvailable) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.rlAmountControllerContainer.visibility = View.INVISIBLE
        }
    }

}