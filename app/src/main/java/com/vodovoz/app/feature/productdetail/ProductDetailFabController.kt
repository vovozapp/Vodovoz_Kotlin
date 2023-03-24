package com.vodovoz.app.feature.productdetail

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.feature.productdetail.viewholders.detailheader.DetailHeader
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceCondition
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText

class ProductDetailFabController(
    private val context: Context
) {

    fun bindFab(
        header: DetailHeader?,
        addIv: AppCompatImageView,
        oldPriceTv: AppCompatTextView,
        miniDetailIv: ImageView,
        currentPriceTv: AppCompatTextView,
        conditionTv: AppCompatTextView
    ) {
        if (header == null) return

        //If left items = 0
        when (header.productDetailUI.leftItems == 0) {
            true -> {
                when (header.replacementProductsCategoryDetail?.productUIList?.isEmpty()) {
                    true -> {
                        addIv.setBackgroundResource(R.drawable.bkg_button_gray_circle_normal)
                        addIv.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.png_alert
                            )
                        )
                    }
                    false -> {
                        addIv.setBackgroundResource(R.drawable.bkg_button_orange_circle_normal)
                        addIv.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_swap
                            )
                        )
                    }
                    else -> {}
                }
            }
            false -> {
                addIv.setBackgroundResource(R.drawable.bkg_button_green_circle_normal)
                addIv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.png_cart))
            }
        }

        oldPriceTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        Glide.with(context)
            .load(header.productDetailUI.detailPictureList.first())
            .into(miniDetailIv)

        var haveDiscount = false
        when (header.productDetailUI.priceUIList.size) {
            1 -> {
                currentPriceTv.setPriceText(header.productDetailUI.priceUIList.first().currentPrice)
                oldPriceTv.setPriceText(header.productDetailUI.priceUIList.first().oldPrice)
                conditionTv.visibility = View.GONE
                if (header.productDetailUI.priceUIList.first().currentPrice <
                    header.productDetailUI.priceUIList.first().oldPrice
                ) haveDiscount = true
            }
            else -> {
                val minimalPrice =
                    header.productDetailUI.priceUIList.maxByOrNull { it.requiredAmount }!!
                currentPriceTv.setMinimalPriceText(minimalPrice.currentPrice)
                conditionTv.setPriceCondition(minimalPrice.requiredAmount)
                conditionTv.visibility = View.VISIBLE
            }
        }
        when (haveDiscount) {
            true -> {
                currentPriceTv.setTextColor(ContextCompat.getColor(context, R.color.red))
                oldPriceTv.visibility = View.VISIBLE
            }
            false -> {
                currentPriceTv.setTextColor(ContextCompat.getColor(context, R.color.text_black))
                oldPriceTv.visibility = View.GONE
            }
        }
    }
}