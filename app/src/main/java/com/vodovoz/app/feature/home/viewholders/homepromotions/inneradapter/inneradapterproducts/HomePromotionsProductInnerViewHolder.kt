package com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter.inneradapterproducts

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
import com.vodovoz.app.databinding.ViewHolderSliderPromotionProductBinding
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.model.ProductUI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomePromotionsProductInnerViewHolder(
    view: View,
    private val clickListener: ProductsClickListener,
    private val cartManager: CartManager,
    private val likeManager: LikeManager
) : ItemViewHolder<ProductUI>(view){

    private val binding: ViewHolderSliderPromotionProductBinding = ViewHolderSliderPromotionProductBinding.bind(view)

    private val amountControllerTimer = object: CountDownTimer(AMOUNT_CONTROLLER_TIMER, AMOUNT_CONTROLLER_TIMER) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            val item = item?: return
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
                .filter{ it.containsKey(item.id) }
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
                .filter{ it.containsKey(item.id) }
                .onEach {
                    item.isFavorite = it[item.id] ?: item.isFavorite
                    bindFav(item)
                }
                .collect()
        }
    }

    init {
        binding.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
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
            if (item.cartQuantity == 0) {
                item.cartQuantity++
                updateCartQuantity(item)
            }
            showAmountController()
        }

        binding.reduceAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
            item.cartQuantity--
            if (item.cartQuantity < 0) item.cartQuantity = 0
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity(item)
        }

        binding.increaseAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
            item.cartQuantity++
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity(item)
        }

        binding.imgFavoriteStatus.setOnClickListener {
            val item = item ?: return@setOnClickListener
            when(item.isFavorite) {
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
        binding.tvName.text = item.name

        //If left items = 0
        binding.add.isSelected = item.leftItems == 0

        //Price per unit / or order quantity
        when(item.pricePerUnit.isNotEmpty()) {
            true -> {
                binding.tvPricePerUnit.visibility = View.VISIBLE
                binding.tvPricePerUnit.text = item.pricePerUnit
            }
            false -> binding.tvPricePerUnit.visibility = View.GONE
        }

        //Price
        when(item.priceList.size) {
            1 -> {
                binding.tvCurrentPrice.text = item.currentPriceStringBuilder
                binding.tvOldPrice.text = item.oldPriceStringBuilder
            }
            else -> {
                binding.tvCurrentPrice.text = item.minimalPriceStringBuilder
                binding.tvPricePerUnit.visibility = View.GONE
            }
        }
        when(item.haveDiscount) {
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
        updateCartQuantity(item)

        //Favorite
        bindFav(item)

        //Status
        when (item.status.isEmpty()) {
            true -> binding.tvStatus.visibility = View.GONE
            false -> {
                binding.tvStatus.visibility = View.VISIBLE
                binding.tvStatus.text = item.status
                binding.tvStatus.background.setTint(Color.parseColor(item.statusColor))
            }
        }

        //DiscountPercent
        when(item.priceList.size == 1 &&
                item.priceList.first().currentPrice < item.priceList.first().oldPrice) {
            true -> {
                binding.tvDiscountPercent.visibility = View.VISIBLE
                binding.tvDiscountPercent.setDiscountPercent(
                    newPrice = item.priceList.first().currentPrice,
                    oldPrice = item.priceList.first().oldPrice
                )
            }
            false -> binding.tvDiscountPercent.visibility = View.GONE
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

    private fun hideAmountController() {
        val product = item ?: return
        if (product.cartQuantity > 0) {
            binding.circleAmount.visibility = View.VISIBLE
        }
        binding.add.visibility = View.VISIBLE
        changeAmountControllerDeployedVisibility(false)
        changePricesContainerVisibility(true)
    }

    private fun updateCartQuantity(item: ProductUI) {
        if (item.cartQuantity < 0) {
            item.cartQuantity = 0
        }

        if (item.cartQuantity <= 0) {
            binding.circleAmount.visibility = View.GONE
        } else {
            if (!binding.amount.isVisible) {
                binding.circleAmount.visibility = View.VISIBLE
            }
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
        binding.tvPricePerUnit.changeVisibilityIfNotEmpty(visible)
        binding.tvCurrentPrice.changeVisibilityIfNotEmpty(visible)
        binding.tvOldPrice.changeVisibilityIfNotEmpty(visible)
        binding.tvPriceCondition.changeVisibilityIfNotEmpty(visible)
    }

    private fun changeAmountControllerDeployedVisibility(visible: Boolean) {
        binding.increaseAmount.isVisible = visible
        binding.reduceAmount.isVisible = visible
        binding.amount.isVisible = visible
    }

    private fun changeStatusesVisibility(visible: Boolean) {
        binding.tvStatus.isVisible = visible
        binding.tvDiscountPercent.isVisible = visible
    }

    fun TextView.changeVisibilityIfNotEmpty(visible: Boolean) {
        this.isVisible = if (this.text.isEmpty()) {
            false
        } else {
            visible
        }
    }
}