package com.vodovoz.app.ui.view_holder

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderProductGridBinding
import com.vodovoz.app.ui.diffUtils.DetailPictureDiffUtilCallback
import com.vodovoz.app.ui.adapter.DetailPicturePagerAdapter
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setDepositPriceText
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setOrderQuantity
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceCondition
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPricePerUnitText
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class ProductGridViewHolder(
    private val binding: ViewHolderProductGridBinding,
    private val onProductClick: (Long) -> Unit,
    private val onChangeCartQuantity: (Long, Int) -> Unit,
    private val onChangeFavoriteStatus: (Long, Boolean) -> Unit,
    private val onNotifyWhenBeAvailable: (Long) -> Unit,
    private val onNotAvailableMore: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val detailPicturePagerAdapter = DetailPicturePagerAdapter(
        iOnProductDetailPictureClick = { onProductClick(productUI.id) }
    )

    private val amountControllerTimer = object: CountDownTimer(3000, 3000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            onChangeCartQuantity(productUI.id, productUI.cartQuantity)
            hideAmountController()
        }
    }

    init {
        binding.tvName.updateLayoutParams<ConstraintLayout.LayoutParams> {
            height = binding.tvName.lineHeight * 3
        }
        binding.root.setOnClickListener { onProductClick(productUI.id) }
        binding.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.pvPictures.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.pvPictures.adapter = detailPicturePagerAdapter

        binding.amountController.add.setOnClickListener {
            if (productUI.leftItems == 0) {
                onNotifyWhenBeAvailable(productUI.id)
                return@setOnClickListener
            }
            if (productUI.cartQuantity == 0) {
                productUI.cartQuantity++
                updateCartQuantity()
            }
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

        TabLayoutMediator(binding.tlIndicators, binding.pvPictures) { _, _ -> }.attach()

        binding.imgFavoriteStatus.setOnClickListener {
            when(productUI.isFavorite) {
                true -> {
                    productUI.isFavorite = false
                    binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_ic_favorite))
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
        binding.amountController.circleAmount.visibility = View.GONE
        binding.amountController.add.visibility = View.GONE
        binding.amountController.amountControllerDeployed.visibility = View.VISIBLE
        amountControllerTimer.start()
    }

    private fun hideAmountController() {
        if (productUI.cartQuantity > 0) {
            binding.amountController.circleAmount.visibility = View.VISIBLE
        }
        binding.amountController.add.visibility = View.VISIBLE
        binding.amountController.amountControllerDeployed.visibility = View.GONE
    }

    private lateinit var productUI: ProductUI

    fun onBind(productUI: ProductUI) {
        this.productUI = productUI

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
                binding.tvPriceCondition.visibility = View.GONE
                if (productUI.priceList.first().currentPrice < productUI.priceList.first().oldPrice || productUI.isGift) haveDiscount = true
            }
            else -> {
                val minimalPrice = productUI.priceList.maxByOrNull { it.requiredAmount }!!
                binding.tvCurrentPrice.setMinimalPriceText(minimalPrice.currentPrice)
                binding.tvPriceCondition.setPriceCondition(minimalPrice.requiredAmount)
                binding.tvPriceCondition.visibility = View.VISIBLE
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

        //Comment
        when (productUI.commentAmount.isEmpty()) {
            true -> binding.tvCommentAmount.text = "Нет отзывов"
            else -> binding.tvCommentAmount.text = productUI.commentAmount
        }

        //Favorite
        when(productUI.isFavorite) {
            false -> binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_ic_favorite))
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

        //DiscountPercent
        when(productUI.priceList.size == 1 &&
                productUI.priceList.first().currentPrice < productUI.priceList.first().oldPrice) {
            true -> {
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
    }

}