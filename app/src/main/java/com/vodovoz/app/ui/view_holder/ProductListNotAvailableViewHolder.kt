package com.vodovoz.app.ui.view_holder

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderProductListNotAvailableBinding
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
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.subjects.PublishSubject

class ProductListNotAvailableViewHolder(
    private val binding: ViewHolderProductListNotAvailableBinding,
    private val onProductClick: (Long) -> Unit,
    private val onSwapProduct: (Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.tvName.updateLayoutParams<LinearLayout.LayoutParams> {
            height = binding.tvName.lineHeight * 2
        }
        binding.root.setOnClickListener { onProductClick(productUI.id) }
        binding.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.imgSwap.setOnClickListener {
            onSwapProduct(productUI.id)
        }
    }

    private lateinit var productUI: ProductUI

    fun onBind(productUI: ProductUI) {
        this.productUI = productUI

        binding.tvName.text = productUI.name

        //Price per unit / or order quantity
        if (productUI.pricePerUnit != 0) {
            binding.tvPricePerUnit.visibility = View.VISIBLE
            binding.tvPricePerUnit.setPricePerUnitText(productUI.pricePerUnit)
        } else {
            binding.tvPricePerUnit.visibility = View.GONE
        }

        //Price
        when(productUI.priceList.size) {
            1 -> {
                binding.tvPrice.setPriceText(productUI.priceList.first().currentPrice, itCanBeGift = true)
                binding.tvOldPrice.setPriceText(productUI.priceList.first().oldPrice)
                binding.tvPriceCondition.visibility = View.GONE
            }
            else -> {
                val minimalPrice = productUI.priceList.maxByOrNull { it.requiredAmount }!!
                binding.tvPrice.setMinimalPriceText(minimalPrice.currentPrice)
                binding.tvPriceCondition.setPriceCondition(minimalPrice.requiredAmount)
                binding.tvPriceCondition.visibility = View.VISIBLE
                binding.tvPricePerUnit.visibility = View.GONE
            }
        }

        //Favorite
        when(productUI.isFavorite) {
            false -> binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_ic_favorite))
            true -> binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_ic_favorite_red))
        }

        //If have deposit
        when(productUI.depositPrice != 0) {
            true -> {
                binding.tvDepositPrice.visibility = View.VISIBLE
                binding.tvDepositPrice.setDepositPriceText(productUI.depositPrice)
            }
            false -> binding.tvDepositPrice.visibility = View.GONE
        }

        Glide
            .with(itemView.context)
            .load(productUI.detailPicture)
            .into(binding.imgPicture)
    }

}