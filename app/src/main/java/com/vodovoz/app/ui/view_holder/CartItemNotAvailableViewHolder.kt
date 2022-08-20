package com.vodovoz.app.ui.view_holder

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderCartItemNotAvailableBinding
import com.vodovoz.app.ui.diffUtils.DetailPictureDiffUtilCallback
import com.vodovoz.app.ui.adapter.DetailPicturePagerAdapter
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setDiscountText
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class CartItemNotAvailableViewHolder(
    private val binding: ViewHolderCartItemNotAvailableBinding,
    private val onProductClickSubject: PublishSubject<Long>,
    private val onSwapClickSubject: PublishSubject<Long>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private val detailPicturePagerAdapter = DetailPicturePagerAdapter(
        iOnProductDetailPictureClick = { onProductClickSubject.onNext(productUI.id) }
    )

    init {
        binding.root.setOnClickListener { onProductClickSubject.onNext(productUI.id) }
        binding.detailPicturePager.setOnClickListener { onProductClickSubject.onNext(productUI.id) }
        binding.oldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.detailPicturePager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.detailPicturePager.adapter = detailPicturePagerAdapter

        TabLayoutMediator(binding.tabIndicator, binding.detailPicturePager) { _, _ -> }.attach()

        binding.swap.setOnClickListener {
            onSwapClickSubject.onNext(productUI.id)
        }
    }

    private lateinit var productUI: ProductUI

    fun onBind(productUI: ProductUI) {
        this.productUI = productUI

        binding.name.text = productUI.name

        when (productUI.status) {
            "" -> binding.statusContainer.visibility = View.GONE
            else -> {
                binding.statusContainer.visibility = View.VISIBLE
                binding.status.text = productUI.status
                binding.statusContainer.setCardBackgroundColor(Color.parseColor(productUI.statusColor))
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
                            binding.spaceBetweenStatusAndTitle.visibility = View.VISIBLE
                        } else {
                            binding.spaceBetweenStatusAndTitle.visibility = View.GONE
                            binding.spaceBetweenStatuses.visibility = View.GONE
                        }
                    }
                }
            }
            else -> {
                if (productUI.status != "") binding.spaceBetweenStatusAndTitle.visibility = View.VISIBLE
                else binding.spaceBetweenStatusAndTitle.visibility = View.GONE
                binding.spaceBetweenStatuses.visibility = View.GONE
                binding.discount.visibility = View.GONE
                binding.price.setPriceText(productUI.priceList.sortedBy { it.requiredAmount }.reversed().find { it.requiredAmount <= productUI.cartQuantity }!!.currentPrice)
            }
        }

        when (productUI.detailPictureList.size) {
            1 -> binding.tabIndicator.visibility = View.GONE
            else -> binding.tabIndicator.visibility = View.VISIBLE
        }

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