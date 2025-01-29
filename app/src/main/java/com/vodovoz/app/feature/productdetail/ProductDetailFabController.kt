package com.vodovoz.app.feature.productdetail

import android.content.Context
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.feature.productdetail.viewholders.detailheader.DetailHeader
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceCondition
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ProductDetailFabController(
    private val context: Context,
    private val viewModel: ProductDetailsFlowViewModel,
    private val navController: NavController,
    private val cartManager: CartManager,
) {

    fun bindFab(
        header: DetailHeader?,
        oldPriceTv: AppCompatTextView,
        miniDetailIv: ImageView,
        currentPriceTv: AppCompatTextView,
        conditionTv: AppCompatTextView,
        amountTv: TextView,
        reduceIv: ImageView,
        increaseIv: ImageView,
        amountDeployed: LinearLayout,
    ) {
        if (header == null) return

        CoroutineScope(Dispatchers.Main).launch {
            cartManager
                .observeCarts()
                .filter { it.containsKey(header.productDetailUI.id) }
                .onEach {
                    header.productDetailUI.cartQuantity =
                        it[header.productDetailUI.id] ?: header.productDetailUI.cartQuantity
                    header.productDetailUI.oldQuantity = header.productDetailUI.cartQuantity
                }
                .collect()
        }

        reduceIv.setOnClickListener {
            reduce(
                header,
                amountTv,
                amountDeployed
            )
        }
        increaseIv.setOnClickListener {
            increase(
                header,
                amountTv,
                amountDeployed
            )
        }

        //If left items = 0

        oldPriceTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        Glide.with(context)
            .load(header.productDetailUI.detailPictureList.first())
            .into(miniDetailIv)

        var haveDiscount = false
        when (header.productDetailUI.priceUIList.size) {
            1 -> {
                currentPriceTv.setPriceText(header.productDetailUI.priceUIList.first().currentPrice.roundToInt())
                oldPriceTv.setPriceText(header.productDetailUI.priceUIList.first().oldPrice.roundToInt())
                conditionTv.visibility = View.GONE
                if (header.productDetailUI.priceUIList.first().currentPrice <
                    header.productDetailUI.priceUIList.first().oldPrice
                ) haveDiscount = true
            }

            else -> {
                val minimalPrice =
                    header.productDetailUI.priceUIList.maxByOrNull { it.requiredAmount }!!
                currentPriceTv.setMinimalPriceText(minimalPrice.currentPrice.roundToInt())
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

        updateFabQuantity(header.productDetailUI.cartQuantity, amountTv, amountDeployed)
    }

    fun updateFabQuantity(
        cartQuantity: Int?,
        amountTv: TextView,
        amountDeployed: LinearLayout,
    ) {
        if (cartQuantity == null) return

        amountTv.text = cartQuantity.toString()
    }

    private fun add(
        header: DetailHeader,
        amountTv: TextView,
        addIv: ImageView,
        amountDeployed: LinearLayout,
    ) {
        if (header.productDetailUI.leftItems == 0) {
            header.replacementProductsCategoryDetail?.let {
                if (it.productUIList.isNotEmpty()) {
                    navController.navigate(
                        ProductDetailsFragmentDirections.actionToReplacementProductsSelectionBS(
                            header.productDetailUI.detailPictureList.first(),
                            it.productUIList.toTypedArray(),
                            header.productDetailUI.id,
                            header.productDetailUI.name
                        )
                    )
                }
            }
        } else {
            if (header.productDetailUI.cartQuantity == 0) {
                header.productDetailUI.cartQuantity++
                updateFabQuantity(header.productDetailUI.cartQuantity, amountTv, amountDeployed)
            }
            showAmountController(
                addIv,
                amountDeployed
            )
        }
    }

    private fun increase(
        header: DetailHeader,
        amountTv: TextView,
        amountDeployed: LinearLayout,
    ) {
        header.productDetailUI.cartQuantity++
        updateFabQuantity(header.productDetailUI.cartQuantity, amountTv, amountDeployed)
    }

    private fun reduce(
        header: DetailHeader,
        amountTv: TextView,
        amountDeployed: LinearLayout,
    ) {
        with(header) {
            val oldQ = productDetailUI.oldQuantity
            productDetailUI.cartQuantity--
            if (productDetailUI.cartQuantity < 0) productDetailUI.cartQuantity = 0
            updateFabQuantity(productDetailUI.cartQuantity, amountTv, amountDeployed)
        }
    }

    private fun showAmountController(
        addIv: ImageView,
        amountDeployed: LinearLayout,
    ) {
        addIv.visibility = View.GONE
        amountDeployed.visibility = View.VISIBLE
    }
}