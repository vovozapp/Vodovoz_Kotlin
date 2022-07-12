package com.vodovoz.app.ui.components.view_holder

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderProductListBinding
import com.vodovoz.app.ui.components.diffUtils.DetailPictureDiffUtilCallback
import com.vodovoz.app.ui.components.adapter.DetailPicturePagerAdapter
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setDiscountText
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class ProductListViewHolder(
    private val binding: ViewHolderProductListBinding,
    private val onProductClickSubject: PublishSubject<Long>,
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>>,
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private val detailPicturePagerAdapter = DetailPicturePagerAdapter(
        iOnProductDetailPictureClick = { onProductClickSubject.onNext(productUI.id) }
    )

    private val amountControllerTimer = object: CountDownTimer(3000, 3000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            onChangeProductQuantitySubject.onNext(Pair(productUI.id, productUI.cartQuantity))
            hideAmountController()
        }
    }

    init {
        binding.root.setOnClickListener { onProductClickSubject.onNext(productUI.id) }
        binding.detailPicturePager.setOnClickListener { onProductClickSubject.onNext(productUI.id) }
        binding.oldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.detailPicturePager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.detailPicturePager.adapter = detailPicturePagerAdapter

        TabLayoutMediator(binding.tabIndicator, binding.detailPicturePager) { _, _ -> }.attach()

        binding.amountController.add.setOnClickListener {
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

        binding.name.text = productUI.name
        binding.price.setPriceText(productUI.newPrice)
        binding.rating.rating = productUI.rating.toFloat()
        binding.amountController.circleAmount.text = productUI.cartQuantity.toString()
        binding.amountController.amount.text = productUI.cartQuantity.toString()

        when (productUI.cartQuantity > 0) {
            true -> binding.amountController.circleAmount.visibility = View.VISIBLE
            false -> binding.amountController.circleAmount.visibility = View.GONE
        }

        when (productUI.commentAmount) {
            "" -> binding.commentAmount.visibility = View.GONE
            else -> {
                binding.commentAmount.visibility = View.VISIBLE
                binding.commentAmount.text = productUI.commentAmount
            }
        }

        when (productUI.status) {
            "" -> binding.statusContainer.visibility = View.GONE
            else -> {
                binding.statusContainer.visibility = View.VISIBLE
                binding.status.text = productUI.status
                binding.statusContainer.setCardBackgroundColor(Color.parseColor(productUI.statusColor.toString()))
            }
        }

        if (productUI.orderQuantity != 0) {
            binding.unitPrice.text = StringBuilder()
                .append("x")
                .append(productUI.orderQuantity)
                .toString()
        }

        when (productUI.oldPrice) {
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

        when (productUI.detailPictureList.size) {
            1 -> binding.tabIndicator.visibility = View.GONE
            else -> binding.tabIndicator.visibility = View.VISIBLE
        }

        if (productUI.oldPrice != 0 && productUI.status != "") binding.spaceBetweenStatuses.visibility = View.VISIBLE
        else binding.spaceBetweenStatuses.visibility = View.GONE

        if (productUI.oldPrice != 0 || productUI.status != "") binding.spaceBetweenStatusAndTitle.visibility = View.VISIBLE
        else binding.spaceBetweenStatusAndTitle.visibility = View.GONE

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