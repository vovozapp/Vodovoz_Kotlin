package com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.list

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
import com.vodovoz.app.databinding.ViewHolderProductListBinding
import com.vodovoz.app.ui.components.base.picturePagerAdapter.DetailPictureDiffUtilCallback
import com.vodovoz.app.ui.components.base.picturePagerAdapter.DetailPictureSliderAdapter
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setDiscountText
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class ProductListViewHolder(
    private val onProductClickSubject: PublishSubject<Long>,
    private val binding: ViewHolderProductListBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private val detailPictureSliderAdapter = DetailPictureSliderAdapter()

    init {
        binding.root.setOnClickListener { onProductClickSubject.onNext(productUI.id) }
        binding.detailPicturePager.setOnClickListener { onProductClickSubject.onNext(productUI.id) }
        binding.oldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.detailPicturePager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.detailPicturePager.adapter = detailPictureSliderAdapter

        TabLayoutMediator(binding.tabIndicator, binding.detailPicturePager) { _, _ -> }.attach()
    }

    private lateinit var productUI: ProductUI

    fun onBind(productUI: ProductUI) {
        this.productUI = productUI

        binding.name.text = productUI.name
        binding.price.setPriceText(productUI.newPrice)
        binding.rating.rating = productUI.rating.toFloat()

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
                binding.statusContainer.setCardBackgroundColor(Color.parseColor(productUI.statusColor))
            }
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
            oldList = detailPictureSliderAdapter.detailPictureUrlList,
            newList = productUI.detailPictureList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            detailPictureSliderAdapter.detailPictureUrlList = productUI.detailPictureList
            diffResult.dispatchUpdatesTo(detailPictureSliderAdapter)
        }
    }

}