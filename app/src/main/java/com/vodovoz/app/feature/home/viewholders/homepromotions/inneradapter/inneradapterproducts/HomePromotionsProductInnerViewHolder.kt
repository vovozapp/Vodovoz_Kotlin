package com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter.inneradapterproducts

import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.databinding.ViewHolderSliderPromotionProductBinding
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.core.network.ApiConfig.AMOUNT_CONTROLLER_TIMER
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPricePerUnitText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import kotlinx.coroutines.flow.*
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
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onProductClick(item.id)
        }

        binding.amountController.add.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
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
            val item = getItemByPosition() ?: return@setOnClickListener
            item.oldQuantity = item.cartQuantity
            item.cartQuantity--
            if (item.cartQuantity < 0) item.cartQuantity = 0
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            clickListener.onChangeProductQuantity(item.id, item.cartQuantity, item.oldQuantity)
            updateCartQuantity(item)
        }

        binding.amountController.increaseAmount.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            item.oldQuantity = item.cartQuantity
            item.cartQuantity++
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            clickListener.onChangeProductQuantity(item.id, item.cartQuantity, item.oldQuantity)
            updateCartQuantity(item)
        }

        binding.imgFavoriteStatus.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
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
        binding.amountController.add.isSelected = item.leftItems == 0

        //Price per unit / or order quantity
        when(item.pricePerUnit != 0) {
            true -> {
                binding.tvPricePerUnit.visibility = View.VISIBLE
                binding.tvPricePerUnit.setPricePerUnitText(item.pricePerUnit)
            }
            false -> binding.tvPricePerUnit.visibility = View.GONE
        }

        //Price
        var haveDiscount = false
        when(item.priceList.size) {
            1 -> {
                binding.tvCurrentPrice.setPriceText(item.priceList.first().currentPrice)
                binding.tvOldPrice.setPriceText(item.priceList.first().oldPrice)
                if (item.priceList.first().currentPrice < item.priceList.first().oldPrice || item.isGift) haveDiscount = true
            }
            else -> {
                val minimalPrice = item.priceList.maxByOrNull { it.requiredAmount }!!
                binding.tvCurrentPrice.setMinimalPriceText(minimalPrice.currentPrice)
                binding.tvPricePerUnit.visibility = View.GONE
            }
        }
        when(haveDiscount) {
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
        var isNotHaveStatuses = true
        when (item.status.isEmpty()) {
            true -> binding.cwStatusContainer.visibility = View.GONE
            false -> {
                binding.cwStatusContainer.visibility = View.VISIBLE
                binding.tvStatus.text = item.status
                binding.cwStatusContainer.setCardBackgroundColor(Color.parseColor(item.statusColor))
            }
        }

        //DiscountPercent
        when(item.priceList.size == 1 &&
                item.priceList.first().currentPrice < item.priceList.first().oldPrice) {
            true -> {
                isNotHaveStatuses = false
                binding.cwDiscountContainer.visibility = View.VISIBLE
                binding.tvDiscountPercent.setDiscountPercent(
                    newPrice = item.priceList.first().currentPrice,
                    oldPrice = item.priceList.first().oldPrice
                )
            }
            false -> binding.cwDiscountContainer.visibility = View.GONE
        }

        when(isNotHaveStatuses) {
            true -> binding.cgStatuses.visibility = View.GONE
            false -> binding.cgStatuses.visibility = View.VISIBLE
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

    private fun getItemByPosition(): ProductUI? {
        return (bindingAdapter as? HomePromotionsProductInnerAdapter)?.getItem(bindingAdapterPosition) as? ProductUI
    }

    private fun hideAmountController() {
        val product = getItemByPosition()
        product?.let {
            if (product.cartQuantity > 0) {
                binding.amountController.circleAmount.visibility = View.VISIBLE
            }
            binding.amountController.add.visibility = View.VISIBLE
            binding.amountController.amountControllerDeployed.visibility = View.INVISIBLE
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
        binding.amountController.circleAmount.visibility = View.INVISIBLE
        binding.amountController.add.visibility = View.INVISIBLE
        binding.amountController.amountControllerDeployed.visibility = View.VISIBLE
        amountControllerTimer.start()
    }
}