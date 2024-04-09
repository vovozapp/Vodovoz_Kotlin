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

class ProductDetailFabController(
    private val context: Context,
    private val viewModel: ProductDetailsFlowViewModel,
    private val navController: NavController,
    private val cartManager: CartManager,
) {

    private var timer: CountDownTimer? = null

    private fun startTimer(
        header: DetailHeader,
        oldQuantity: Int,
        circleAmountTv: TextView,
        addIv: ImageView,
        amountDeployed: LinearLayout,
    ) {
        timer?.cancel()
        timer = object : CountDownTimer(2000, 2000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                viewModel.changeCart(
                    productId = header.productDetailUI.id,
                    quantity = header.productDetailUI.cartQuantity,
                    oldQuan = oldQuantity
                )
                hideAmountController(header, circleAmountTv, addIv, amountDeployed)
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
        conditionTv: AppCompatTextView,
        amountTv: TextView,
        circleAmountTv: TextView,
        addIv: ImageView,
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
                    updateFabQuantity(header.productDetailUI.cartQuantity, amountTv, circleAmountTv)
                }
                .collect()
        }

        addIv.setOnClickListener {
            add(
                header,
                amountTv,
                circleAmountTv,
                addIv,
                amountDeployed
            )
        }
        reduceIv.setOnClickListener {
            reduce(
                header,
                amountTv,
                circleAmountTv,
                addIv,
                amountDeployed
            )
        }
        increaseIv.setOnClickListener {
            increase(
                header,
                amountTv,
                circleAmountTv,
                addIv,
                amountDeployed
            )
        }

        //If left items = 0
        when (header.productDetailUI.leftItems == 0) {
            true -> {
                when (header.replacementProductsCategoryDetail?.productUIList?.isEmpty()) {
                    true -> {
                        addIv.isSelected = true
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
                addIv.isSelected = false
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

        updateFabQuantity(header.productDetailUI.cartQuantity, amountTv, circleAmountTv)
    }

    fun updateFabQuantity(
        cartQuantity: Int?,
        amountTv: TextView,
        circleAmountTv: TextView,
    ) {
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

    private fun add(
        header: DetailHeader,
        amountTv: TextView,
        circleAmountTv: TextView,
        addIv: ImageView,
        amountDeployed: LinearLayout,
    ) {
        val oldQ = header.productDetailUI.oldQuantity
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
                updateFabQuantity(header.productDetailUI.cartQuantity, amountTv, circleAmountTv)
            }
            showAmountController(
                header,
                oldQ,
                circleAmountTv,
                addIv,
                amountDeployed
            )
        }
    }

    private fun increase(
        header: DetailHeader,
        amountTv: TextView,
        circleAmountTv: TextView,
        addIv: ImageView,
        amountDeployed: LinearLayout,
    ) {
        val oldQ = header.productDetailUI.oldQuantity
        header.productDetailUI.cartQuantity++
        startTimer(header, oldQ, circleAmountTv, addIv, amountDeployed)
        updateFabQuantity(header.productDetailUI.cartQuantity, amountTv, circleAmountTv)
    }

    private fun reduce(
        header: DetailHeader,
        amountTv: TextView,
        circleAmountTv: TextView,
        addIv: ImageView,
        amountDeployed: LinearLayout,
    ) {
        with(header) {
            val oldQ = productDetailUI.oldQuantity
            productDetailUI.cartQuantity--
            if (productDetailUI.cartQuantity < 0) productDetailUI.cartQuantity = 0
            startTimer(header, oldQ, circleAmountTv, addIv, amountDeployed)
            updateFabQuantity(productDetailUI.cartQuantity, amountTv, circleAmountTv)
        }
    }

    private fun showAmountController(
        header: DetailHeader,
        oldQ: Int,
        circleAmountTv: TextView,
        addIv: ImageView,
        amountDeployed: LinearLayout,
    ) {
        circleAmountTv.visibility = View.GONE
        addIv.visibility = View.GONE
        amountDeployed.visibility = View.VISIBLE
        startTimer(header, oldQ, circleAmountTv, addIv, amountDeployed)
    }

    internal fun hideAmountController(
        header: DetailHeader,
        circleAmountTv: TextView,
        addIv: ImageView,
        amountDeployed: LinearLayout,
    ) {
        if (header.productDetailUI.cartQuantity > 0) {
            circleAmountTv.visibility = View.VISIBLE
        }
        addIv.visibility = View.VISIBLE
        amountDeployed.visibility = View.GONE
    }
}