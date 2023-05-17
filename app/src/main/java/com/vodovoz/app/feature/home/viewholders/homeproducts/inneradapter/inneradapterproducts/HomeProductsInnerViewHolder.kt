package com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts

import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.databinding.ViewHolderSliderProductBinding
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.core.network.ApiConfig.AMOUNT_CONTROLLER_TIMER
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPricePerUnitText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeProductsInnerViewHolder(
    view: View,
    private val clickListener: ProductsClickListener,
    private val cartManager: CartManager,
    private val likeManager: LikeManager
) : ItemViewHolder<ProductUI>(view) {

    private val binding: ViewHolderSliderProductBinding = ViewHolderSliderProductBinding.bind(view)

    private val amountControllerTimer = object: CountDownTimer(AMOUNT_CONTROLLER_TIMER, AMOUNT_CONTROLLER_TIMER) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
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
        binding.llPricesContainer.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onProductClick(item.id)
        }

        binding.amountController.add.setOnClickListener {
            val item = item ?: return@setOnClickListener
            if (item.leftItems == 0) {
                clickListener.onNotifyWhenBeAvailable(item.id, item.name, item.detailPicture)
                return@setOnClickListener
            }
            if (item.cartQuantity == 0) {
                item.oldQuantity = item.cartQuantity
                item.cartQuantity++
                clickListener.onChangeProductQuantity(item.id, item.cartQuantity, item.oldQuantity)
                updateCartQuantity(item)
            }
            showAmountController()
        }

        binding.amountController.reduceAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
            item.oldQuantity = item.cartQuantity
            item.cartQuantity--
            if (item.cartQuantity < 0) item.cartQuantity = 0
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            clickListener.onChangeProductQuantity(item.id, item.cartQuantity, item.oldQuantity)
            updateCartQuantity(item)
        }

        binding.amountController.increaseAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
            item.oldQuantity = item.cartQuantity
            item.cartQuantity++
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            clickListener.onChangeProductQuantity(item.id, item.cartQuantity, item.oldQuantity)
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

        binding.amountController.add.isSelected = item.leftItems == 0

        //Price per unit / or order quantity
        binding.llPricesContainer.tvPricePerUnit.isVisible = item.pricePerUnit != 0
        binding.llPricesContainer.tvPricePerUnit.text = item.pricePerUnitStringBuilder

        //Price
        when(item.priceList.size) {
            1 -> {
                binding.llPricesContainer.tvCurrentPrice.text = item.currentPriceStringBuilder
                binding.llPricesContainer.tvOldPrice.text = item.oldPriceStringBuilder
            }
            else -> {
                binding.llPricesContainer.tvCurrentPrice.text = item.minimalPriceStringBuilder
                binding.llPricesContainer.tvPricePerUnit.visibility = View.GONE
            }
        }
        when(item.haveDiscount) {
            true -> {
                binding.llPricesContainer.tvCurrentPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                binding.llPricesContainer.tvOldPrice.visibility = View.VISIBLE
            }
            false -> {
                binding.llPricesContainer.tvCurrentPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_black))
                binding.llPricesContainer.tvOldPrice.visibility = View.GONE
            }
        }

        //Cart amount
        updateCartQuantity(item)

        //Favorite
        bindFav(item)

        //Status
        var isNotHaveStatuses = true
        when (item.status.isEmpty()) {
            true -> binding.cgStatuses.cwStatusContainer.visibility = View.GONE
            false -> {
                binding.cgStatuses.cwStatusContainer.visibility = View.VISIBLE
                binding.cgStatuses.tvStatus.text = item.status
                binding.cgStatuses.cwStatusContainer.setCardBackgroundColor(Color.parseColor(item.statusColor))
            }
        }

        //DiscountPercent
        when(item.priceList.size == 1 &&
                item.priceList.first().currentPrice < item.priceList.first().oldPrice) {
            true -> {
                isNotHaveStatuses = false
                binding.cgStatuses.cwDiscountContainer.visibility = View.VISIBLE
                binding.cgStatuses.tvDiscountPercent.text = item.discountPercentStringBuilder
            }
            false -> binding.cgStatuses.cwDiscountContainer.visibility = View.GONE
        }

        when(isNotHaveStatuses) {
            true -> binding.cgStatuses.root.visibility = View.GONE
            false -> binding.cgStatuses.root.visibility = View.VISIBLE
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
        val product = item
        product?.let {
            if (product.cartQuantity > 0) {
                binding.amountController.circleAmount.visibility = View.VISIBLE
            }
            binding.amountController.add.visibility = View.VISIBLE
            binding.amountController.amountControllerDeployed.visibility = View.INVISIBLE
            binding.llPricesContainer.root.visibility = View.VISIBLE
        }
    }

    private fun updateCartQuantity(item: ProductUI) {
        if (item.cartQuantity < 0) {
            item.cartQuantity = 0
        }

        if (item.cartQuantity <= 0) {
            binding.amountController.circleAmount.visibility = View.GONE
        } else {
            binding.amountController.circleAmount.visibility = View.VISIBLE
        }


        binding.amountController.amount.text = item.cartQuantity.toString()
        binding.amountController.circleAmount.text = item.cartQuantity.toString()
    }

    private fun showAmountController() {
        binding.llPricesContainer.root.visibility = View.INVISIBLE
        binding.amountController.circleAmount.visibility = View.INVISIBLE
        binding.amountController.add.visibility = View.INVISIBLE
        binding.amountController.amountControllerDeployed.visibility = View.VISIBLE
        amountControllerTimer.start()
    }
}