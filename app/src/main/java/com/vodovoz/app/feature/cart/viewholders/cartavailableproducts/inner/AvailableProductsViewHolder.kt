package com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner

import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.View
import android.widget.RatingBar
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.core.network.ApiConfig.AMOUNT_CONTROLLER_TIMER
import com.vodovoz.app.databinding.ViewHolderProductCartBinding
import com.vodovoz.app.databinding.ViewHolderProductListBinding
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowClickListener
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowPagerAdapter
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPicturePager
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDepositPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setOrderQuantity
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class AvailableProductsViewHolder(
    view: View,
    val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager,
    private val cartManager: CartManager,
    private val ratingProductManager: RatingProductManager,
) : ItemViewHolder<ProductUI>(view) {

    private val binding: ViewHolderProductCartBinding = ViewHolderProductCartBinding.bind(view)

    override fun attach() {
        super.attach()

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
    }

    init {

        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            debugLog { "isgift ${item.isGift} isBottle ${item.isBottle}" }
            if (item.priceList.first().currentPrice <= 1 || item.isGift || item.isBottle) return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }

        binding.ivPicture.setOnClickListener {
            val item = item ?: return@setOnClickListener
            debugLog { "isgift ${item.isGift} isBottle ${item.isBottle}" }
            if (item.priceList.first().currentPrice <= 1 || item.isGift || item.isBottle) return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }

        binding.amountController.reduceAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
            item.cartQuantity--
            if (item.cartQuantity < 0) item.cartQuantity = 0
            updateCartQuantity(item)
            onChangeProductQuantity(item)
        }

        binding.amountController.increaseAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
            item.cartQuantity++
            updateCartQuantity(item)
            onChangeProductQuantity(item)
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
            productsClickListener.onFavoriteClick(item.id, item.isFavorite)
        }
    }

    override fun bind(item: ProductUI) {
        super.bind(item)

        binding.imgFavoriteStatus.visibility = View.VISIBLE
        if (item.canBuy) {
            binding.amountController.amountControllerDeployed.visibility = View.VISIBLE
        } else {
            binding.amountController.amountControllerDeployed.visibility = View.GONE
        }

        binding.tvName.text = item.name
        val article = "Артикул: ${item.id}"
        binding.tvArticle.text = article

        updateCartQuantity(item)

        bindFav(item)

        when (item.depositPrice != 0) {
            true -> {
                binding.tvDepositPrice.visibility = View.VISIBLE
                binding.tvDepositPrice.setDepositPriceText(item.depositPrice)
            }

            false -> binding.tvDepositPrice.visibility = View.GONE
        }
        if(item.giftText.isNotEmpty())  {
            binding.flDepositContainer.visibility = View.GONE
            binding.tvGiftMessage.visibility = View.VISIBLE
        } else{
            binding.flDepositContainer.visibility = View.VISIBLE
            binding.tvGiftMessage.visibility = View.GONE
        }

        //UpdatePictures
        Glide
            .with(itemView.context)
            .load(item.detailPicture)
            .into(binding.ivPicture)

        //If is bottle
        if (item.isBottle) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.tvDepositPrice.visibility = View.VISIBLE
            binding.tvDepositPrice.setDepositPriceText(item.priceList.first().currentPrice.roundToInt())
            /*if (item.priceList.first().currentPrice > 0) {
                binding.clPricesContainer.visibility = View.VISIBLE
            } else {
                binding.clPricesContainer.visibility = View.GONE
            }*/ //todo
        }

        //If is gift
        if (item.isGift) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE

        }
        if (item.forCart) {

        }

        binding.imgFavoriteStatus.isVisible =
            !(item.chipsBan == 1 || item.chipsBan == 3 || item.chipsBan == 5)

        //If is not available
        if (!item.isAvailable) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.amountController.root.visibility = View.INVISIBLE
        }

    }

    private fun bindFav(item: ProductUI) {
        when (item.isFavorite) {
            false -> binding.imgFavoriteStatus.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.ic_favorite_black
                )
            )

            true -> binding.imgFavoriteStatus.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.png_ic_favorite_red
                )
            )
        }
    }

    private fun showAmountController(item: ProductUI?) {
        if (item != null) {
            if (item.cartQuantity > 0) {
                if (item.canBuy) {
                    binding.amountController.amountControllerDeployed.visibility = View.VISIBLE
                } else {
                    binding.amountController.deleteButton.visibility = View.VISIBLE
                }
            }
            else{
                binding.amountController.deleteButton.visibility = View.INVISIBLE
                binding.amountController.amountControllerDeployed.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateCartQuantity(item: ProductUI) {
        if (item.cartQuantity < 0) {
            item.cartQuantity = 0
        }
        if (item.oldQuantity == 0) {
            item.oldQuantity = 1
        }
        binding.amountController.amount.text = item.cartQuantity.toString()
        showAmountController(item)
        debugLog { "updateCartQuantity: ${item.oldQuantity} -> ${item.cartQuantity}" }
    }

    private fun onChangeProductQuantity(item: ProductUI){
        productsClickListener.onChangeProductQuantity(
            item.id,
            item.cartQuantity,
            item.oldQuantity
        )
    }
}