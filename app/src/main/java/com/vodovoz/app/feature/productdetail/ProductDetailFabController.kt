package com.vodovoz.app.feature.productdetail

import android.content.Context
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.feature.productdetail.viewholders.detailheader.DetailHeader
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceCondition
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished


class ProductDetailFabController(
    private val context: Context,
    private val amountTv: TextView,
    private val circleAmountTv: TextView,
    private val viewModel: ProductDetailsFlowViewModel,
    private val navController: NavController,
    private val addIv: AppCompatImageView,
    private val reduceIv: ImageView,
    private val increaseIv: ImageView,
    private val amountDeployed: LinearLayout
) {

    private var timer: CountDownTimer? = null

    private fun startTimer(header: DetailHeader, oldQuantity: Int) {
        timer?.cancel()
        timer = object: CountDownTimer(3000, 3000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                viewModel.changeCart(
                    productId = header.productDetailUI.id,
                    quantity = header.productDetailUI.cartQuantity,
                    oldQuan = oldQuantity
                )
                hideAmountController(header)
            }
        }
        timer?.start()
    }

    fun stopTimer() {
        timer?.cancel()
    }

    fun bindFab(
        header: DetailHeader?,
        oldPriceTv: AppCompatTextView,
        miniDetailIv: ImageView,
        currentPriceTv: AppCompatTextView,
        conditionTv: AppCompatTextView
    ) {
        if (header == null) return

        addIv.setOnClickListener { add(header) }
        reduceIv.setOnClickListener { reduce(header) }
        increaseIv.setOnClickListener { increase(header) }

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

        updateFabQuantity(header.productDetailUI.cartQuantity)
    }

    fun updateFabQuantity(cartQuantity: Int?) {
        if (cartQuantity == null) return

        amountTv.text = cartQuantity.toString()
        circleAmountTv.text = cartQuantity.toString()
        when (cartQuantity > 0) {
            true -> {
                circleAmountTv.visibility = View.VISIBLE
            }
            false -> {
                circleAmountTv.visibility = View.GONE
            }
        }
    }

    private fun add(header: DetailHeader) {
        val oldQ = header.productDetailUI.cartQuantity
        if (header.productDetailUI.leftItems == 0) {
            header.replacementProductsCategoryDetail?.let {
                if (it.productUIList.isNotEmpty()) {
                    navController.navigate(ProductDetailsFragmentDirections.actionToReplacementProductsSelectionBS(
                        header.productDetailUI.detailPictureList.first(),
                        it.productUIList.toTypedArray(),
                        header.productDetailUI.id,
                        header.productDetailUI.name
                    ))
                }
            }
        } else {
            if (header.productDetailUI.cartQuantity == 0) {
                header.productDetailUI.cartQuantity++
                updateFabQuantity(header.productDetailUI.cartQuantity)
            }
            showAmountController(header, oldQ)
        }
    }

    private fun increase(header: DetailHeader) {
        val oldQ = header.productDetailUI.cartQuantity
        header.productDetailUI.cartQuantity++
        startTimer(header, oldQ)
        updateFabQuantity(header.productDetailUI.cartQuantity)
    }

    private fun reduce(header: DetailHeader) {
        with(header) {
            val oldQ = productDetailUI.cartQuantity
            productDetailUI.cartQuantity--
            if (productDetailUI.cartQuantity < 0) productDetailUI.cartQuantity = 0
            startTimer(header, oldQ)
            updateFabQuantity(productDetailUI.cartQuantity)
        }
    }

    private fun showAmountController(header: DetailHeader, oldQ: Int) {
        circleAmountTv.visibility = View.GONE
        addIv.visibility = View.GONE
        amountDeployed.visibility = View.VISIBLE
        startTimer(header, oldQ)
    }

    private fun hideAmountController(header: DetailHeader) {
        if (header.productDetailUI.cartQuantity > 0) {
            circleAmountTv.visibility = View.VISIBLE
        }
        addIv.visibility = View.VISIBLE
        amountDeployed.visibility = View.GONE
    }
}