package com.vodovoz.app.feature.productlist.viewholders

import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.View
import android.widget.RatingBar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.core.network.ApiConfig.AMOUNT_CONTROLLER_TIMER
import com.vodovoz.app.databinding.ViewHolderProductGridBinding
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowClickListener
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowPagerAdapter
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPicturePager
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner.AvailableProductsAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.productlist.adapter.SortedAdapter
import com.vodovoz.app.ui.diffUtils.DetailPictureDiffUtilCallback
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setLimitedText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceCondition
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPricePerUnitText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductsGridViewHolder(
    view: View,
    val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager,
    private val cartManager: CartManager,
    private val ratingProductManager: RatingProductManager
) : ItemViewHolder<ProductUI>(view) {

    private val binding: ViewHolderProductGridBinding = ViewHolderProductGridBinding.bind(view)

    private val amountControllerTimer = object : CountDownTimer(AMOUNT_CONTROLLER_TIMER, AMOUNT_CONTROLLER_TIMER) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            val item = item ?: return
            productsClickListener.onChangeProductQuantity(item.id, item.cartQuantity, item.oldQuantity)
            hideAmountController(item)
        }
    }

    private val detailPictureFlowPagerAdapter = DetailPictureFlowPagerAdapter(
        clickListener = object : DetailPictureFlowClickListener {
            override fun onDetailPictureClick() {
                val item = item ?: return
                productsClickListener.onProductClick(item.id)
            }

            override fun onProductClick(id: Long) {
                val item = item ?: return
                productsClickListener.onProductClick(item.id)
            }
        }
    )

    override fun attach() {
        super.attach()

        launch {
            val item = item ?: return@launch
            ratingProductManager
                .observeRatings()
                .filter{ it.containsKey(item.id) }
                .onEach {
                    item.rating = it[item.id] ?: item.rating
                    binding.rbRating.rating = item.rating
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
    }

    init {
        binding.pvPictures.setOnClickListener {
            val item = item ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }
        binding.pvPictures.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.pvPictures.adapter = detailPictureFlowPagerAdapter

        TabLayoutMediator(binding.tlIndicators, binding.pvPictures) { _, _ -> }.attach()

        binding.llPricesContainer.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }

        binding.pvPictures.setOnClickListener {
            val item = item ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }

        binding.amountController.add.setOnClickListener {
            val item = item ?: return@setOnClickListener
            if (item.isGift) return@setOnClickListener
            if (item.leftItems == 0) {
                productsClickListener.onNotifyWhenBeAvailable(item.id, item.name, item.detailPicture)
                return@setOnClickListener
            }

            item.oldQuantity = item.cartQuantity
            item.cartQuantity++
            updateCartQuantity(item)
            showAmountController()
        }

        binding.amountController.reduceAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
            item.oldQuantity = item.cartQuantity
            item.cartQuantity--
            if (item.cartQuantity < 0) item.cartQuantity = 0
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity(item)
        }

        binding.amountController.increaseAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
            item.oldQuantity = item.cartQuantity
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
            productsClickListener.onFavoriteClick(item.id, item.isFavorite)
        }

        RatingBar.OnRatingBarChangeListener { p0, newRating, p2 ->
            val item = item ?: return@OnRatingBarChangeListener
            if (newRating != binding.rbRating.rating) {
                productsClickListener.onChangeRating(item.id, newRating, item.rating)
            }
        }.also { binding.rbRating.onRatingBarChangeListener = it }
    }

    override fun bind(item: ProductUI) {
        super.bind(item)

        bindFav(item)

        binding.tvName.setLimitedText(item.name)
        binding.rbRating.rating = item.rating

        binding.amountController.add.isSelected = item.leftItems == 0

        //Price per unit / or order quantity
        when(item.pricePerUnit != 0) {
            true -> {
                binding.llPricesContainer.tvPricePerUnit.visibility = View.VISIBLE
                binding.llPricesContainer.tvPricePerUnit.text = item.pricePerUnitStringBuilder
            }
            false -> binding.llPricesContainer.tvPricePerUnit.visibility = View.GONE
        }

        //Price
        when(item.priceList.size) {
            1 -> {
                binding.llPricesContainer.tvCurrentPrice.text = item.currentPriceStringBuilder
                binding.llPricesContainer.tvOldPrice.text = item.oldPriceStringBuilder
                binding.llPricesContainer.tvPriceCondition.visibility = View.GONE
            }
            else -> {
                binding.llPricesContainer.tvCurrentPrice.text = item.minimalPriceStringBuilder
                binding.llPricesContainer.tvPriceCondition.text = item.priceConditionStringBuilder
                binding.llPricesContainer.tvPriceCondition.visibility = View.VISIBLE
                binding.llPricesContainer.tvPricePerUnit.visibility = View.GONE
            }
        }

        when(item.haveDiscount) {
            true -> {
                binding.llPricesContainer.tvCurrentPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                binding.llPricesContainer.tvOldPrice.visibility = View.VISIBLE
            }
            false -> {
                binding.llPricesContainer.tvCurrentPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_new_black))
                binding.llPricesContainer.tvOldPrice.visibility = View.GONE
            }
        }

        //Cart amount
        binding.amountController.circleAmount.text = item.cartQuantity.toString()
        binding.amountController.amount.text = item.cartQuantity.toString()

        when (item.cartQuantity > 0) {
            true -> binding.amountController.circleAmount.visibility = View.VISIBLE
            false -> binding.amountController.circleAmount.visibility = View.GONE
        }

        //Comment
        when (item.commentAmount.isEmpty()) {
            true -> binding.tvCommentAmount.text = ""
            else -> binding.tvCommentAmount.text = item.commentAmount
        }

        //Status
        var isNotHaveStatuses = true
        when (item.status.isEmpty()) {
            true -> binding.cgStatuses.cwStatusContainer.visibility = View.GONE
            false -> {
                isNotHaveStatuses = false
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
        binding.tlIndicators.isVisible = item.detailPictureList.size != 1

        detailPictureFlowPagerAdapter.submitList(item.detailPictureListPager)

    }

    private fun bindFav(item: ProductUI) {
        binding.imgFavoriteStatus.isSelected = item.isFavorite
    }

    private fun showAmountController() {
        binding.amountController.circleAmount.visibility = View.INVISIBLE
        binding.amountController.add.visibility = View.INVISIBLE
        binding.amountController.amountControllerDeployed.visibility = View.VISIBLE
        amountControllerTimer.start()
    }

    private fun hideAmountController(item: ProductUI) {
        if (item.cartQuantity > 0) {
            binding.amountController.circleAmount.visibility = View.VISIBLE
        }
        binding.amountController.add.visibility = View.VISIBLE
        binding.amountController.amountControllerDeployed.visibility = View.INVISIBLE
    }

    private fun updateCartQuantity(item: ProductUI) {
        if (item.cartQuantity < 0) {
            item.cartQuantity = 0
        }
        binding.amountController.amount.text = item.cartQuantity.toString()
        binding.amountController.circleAmount.text = item.cartQuantity.toString()
    }
}