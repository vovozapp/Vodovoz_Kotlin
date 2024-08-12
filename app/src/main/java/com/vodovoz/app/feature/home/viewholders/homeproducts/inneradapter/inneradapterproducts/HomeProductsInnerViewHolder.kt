package com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts

import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.core.network.ApiConfig.AMOUNT_CONTROLLER_TIMER
import com.vodovoz.app.databinding.ViewHolderSliderProductBinding
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.view.LabelChip
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeProductsInnerViewHolder(
    view: View,
    private val clickListener: ProductsClickListener,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
) : ItemViewHolder<ProductUI>(view) {

    private val binding: ViewHolderSliderProductBinding = ViewHolderSliderProductBinding.bind(view)

    private val amountControllerTimer =
        object : CountDownTimer(AMOUNT_CONTROLLER_TIMER, AMOUNT_CONTROLLER_TIMER) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {

                val item = item ?: return
                debugLog { "onChangeProductQuantity ${item.cartQuantity}, ${item.oldQuantity}" }
                clickListener.onChangeProductQuantity(item.id, item.cartQuantity, item.oldQuantity)
                hideAmountController()
            }
        }

    override fun attach() {
        super.attach()

        launch {
            val item = item ?: return@launch
            cartManager
                .observeCarts()
                .filter { it.containsKey(item.id) }
                .onEach {
                    item.cartQuantity = it[item.id] ?: item.cartQuantity
                    item.oldQuantity = item.cartQuantity
                    updateCartQuantity(item)
                }
                .collect()
        }

        launch {
            val item = item ?: return@launch
            likeManager
                .observeLikes()
                .filter { it.containsKey(item.id) }
                .onEach {
                    item.isFavorite = it[item.id] ?: item.isFavorite
                    bindFav(item)
                }
                .collect()
        }
    }

    init {
        binding.priceContainer.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onProductClick(item.id)
        }

        binding.add.setOnClickListener {
            val item = item ?: return@setOnClickListener
            if (item.leftItems == 0) {
                clickListener.onNotifyWhenBeAvailable(item.id, item.name, item.detailPicture)
                return@setOnClickListener
            }
//            item.oldQuantity = item.cartQuantity
            if (item.cartQuantity == 0) {
                item.cartQuantity++
            }
            updateCartQuantity(item)
            showAmountController()
        }

        binding.reduceAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
//            item.oldQuantity = item.cartQuantity
            item.cartQuantity--
            if (item.cartQuantity < 0) item.cartQuantity = 0
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity(item)
        }

        binding.increaseAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
//            item.oldQuantity = item.cartQuantity
            item.cartQuantity++
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity(item)
        }

        binding.imgFavoriteStatus.setOnClickListener {
            val item = item ?: return@setOnClickListener
            when (item.isFavorite) {
                true -> {
                    item.isFavorite = false
                    binding.imgFavoriteStatus.isSelected = false
                }

                false -> {
                    item.isFavorite = true
                    binding.imgFavoriteStatus.isSelected = true
                }
            }
            clickListener.onFavoriteClick(item.id, item.isFavorite)
        }
    }

    override fun bind(item: ProductUI) {
        super.bind(item)
        binding.priceContainer.tvPriceCondition.visibility = View.GONE

        binding.add.isSelected = item.leftItems == 0

        //Price per unit / or order quantity
        binding.priceContainer.tvPricePerUnit.isVisible = item.pricePerUnit.isNotEmpty()
        binding.priceContainer.tvPricePerUnit.text = item.pricePerUnit

        //Price
        when (item.priceList.size) {
            1 -> {
                binding.priceContainer.tvCurrentPrice.text = item.currentPriceStringBuilder
                binding.priceContainer.tvOldPrice.text = item.oldPriceStringBuilder
            }

            else -> {
                if (item.conditionPrice.isNotEmpty()) {
                    binding.priceContainer.tvCurrentPrice.text = item.conditionPrice
                } else {
                    binding.priceContainer.tvCurrentPrice.text = item.minimalPriceStringBuilder
                    binding.priceContainer.tvPricePerUnit.visibility = View.GONE
                }
            }
        }
        when (item.haveDiscount) {
            true -> {
                binding.priceContainer.tvCurrentPrice.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.red
                    )
                )
                binding.priceContainer.tvOldPrice.visibility = View.VISIBLE
            }

            false -> {
                binding.priceContainer.tvCurrentPrice.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.text_black
                    )
                )
                binding.priceContainer.tvOldPrice.visibility = View.GONE
            }
        }

        //Cart amount
        updateCartQuantity(item)

        //Favorite
        bindFav(item)


        //Status
        if(item.labels.isNotEmpty()){
            binding.chipGroup.removeAllViews()
            binding.chipGroup.visibility = View.VISIBLE
            binding.cgStatuses.root.visibility = View.GONE
            for (label in item.labels) {
                val chip = LabelChip(binding.root.context)
                chip.text = label.name
                chip.color = Color.parseColor(label.color)
                binding.chipGroup.addView(chip)
            }
        } else {
            binding.chipGroup.visibility = View.GONE
            when (item.status.isEmpty()) {
                true -> binding.cgStatuses.cwStatusContainer.visibility = View.GONE
                false -> {
                    binding.cgStatuses.cwStatusContainer.visibility = View.VISIBLE
                    binding.cgStatuses.tvStatus.text = item.status
                    binding.cgStatuses.cwStatusContainer.setCardBackgroundColor(
                        Color.parseColor(
                            item.statusColor
                        )
                    )
                }
            }

            //DiscountPercent
            if (item.priceList.size == 1 &&
                item.priceList.first().currentPrice < item.priceList.first().oldPrice
            ) {
                binding.cgStatuses.tvDiscountPercent.visibility = View.VISIBLE
                binding.cgStatuses.tvDiscountPercent.text = item.discountPercentStringBuilder
            } else {
                binding.cgStatuses.tvDiscountPercent.visibility = View.GONE
            }
        }

        //UpdatePictures
        Glide
            .with(itemView.context)
            .load(item.detailPicture)
            .into(binding.imgPicture)
    }

    private fun bindFav(item: ProductUI) {
        binding.imgFavoriteStatus.isSelected = item.isFavorite
    }

    internal fun hideAmountController() {
        val product = item
        product?.let {
            if (product.cartQuantity > 0) {
                binding.circleAmount.visibility = View.VISIBLE
            }
            binding.add.visibility = View.VISIBLE
            changeAmountControllerDeployedVisibility(false)
            changePricesContainerVisibility(true)
        }
    }

    private fun updateCartQuantity(item: ProductUI) {
        if (item.cartQuantity < 0) {
            item.cartQuantity = 0
        }

        if (item.cartQuantity <= 0) {
            binding.circleAmount.visibility = View.GONE
        } else if (!binding.amount.isVisible) {
            binding.circleAmount.visibility = View.VISIBLE
        }

        binding.amount.text = item.cartQuantity.toString()
        binding.circleAmount.text = item.cartQuantity.toString()
    }

    private fun showAmountController() {
        changePricesContainerVisibility(false)
        binding.circleAmount.visibility = View.GONE
        binding.add.visibility = View.GONE
        changeAmountControllerDeployedVisibility(true)
        amountControllerTimer.start()
    }

    private fun changePricesContainerVisibility(visible: Boolean) {
        binding.priceContainer.tvPricePerUnit.changeVisibilityIfNotEmpty(visible)
        binding.priceContainer.tvCurrentPrice.changeVisibilityIfNotEmpty(visible)
        binding.priceContainer.tvOldPrice.changeVisibilityIfNotEmpty(visible)
        binding.priceContainer.tvPriceCondition.changeVisibilityIfNotEmpty(visible)
    }

    private fun changeAmountControllerDeployedVisibility(visible: Boolean) {
        binding.increaseAmount.isVisible = visible
        binding.reduceAmount.isVisible = visible
        binding.amount.isVisible = visible
    }

    private fun changeStatusesVisibility(visible: Boolean) {
        binding.cgStatuses.cwStatusContainer.isVisible = visible
        binding.cgStatuses.cwDiscountContainer.isVisible = visible
    }

    fun TextView.changeVisibilityIfNotEmpty(visible: Boolean) {
        this.isVisible = if (this.text.isEmpty()) {
            false
        } else {
            visible
        }
    }
}