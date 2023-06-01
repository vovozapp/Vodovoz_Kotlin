package com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner

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
import com.vodovoz.app.databinding.ViewHolderProductListBinding
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowClickListener
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowPagerAdapter
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPicturePager
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.diffUtils.DetailPictureDiffUtilCallback
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDepositPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setOrderQuantity
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPricePerUnitText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AvailableProductsViewHolder(
    view: View,
    val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager,
    private val cartManager: CartManager,
    private val ratingProductManager: RatingProductManager
) : ItemViewHolder<ProductUI>(view) {

    private val binding: ViewHolderProductListBinding = ViewHolderProductListBinding.bind(view)

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
                if (item.priceList.first().currentPrice <= 1 || item.isGift || item.isBottle) return
                productsClickListener.onProductClick(item.id)
            }

            override fun onProductClick(id: Long) {
                val item = item ?: return
                if (item.priceList.first().currentPrice <= 1 || item.isGift || item.isBottle) return
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
        binding.vpPictures.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpPictures.adapter = detailPictureFlowPagerAdapter

        binding.tlIndicators.attachTo(binding.vpPictures)

        binding.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            debugLog { "isgift ${item.isGift} isBottle ${item.isBottle}" }
            if (item.priceList.first().currentPrice <= 1 || item.isGift || item.isBottle) return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }

        binding.vpPictures.setOnClickListener {
            val item = item ?: return@setOnClickListener
            debugLog { "isgift ${item.isGift} isBottle ${item.isBottle}" }
            if (item.priceList.first().currentPrice <= 1 || item.isGift || item.isBottle) return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }

        binding.amountController.add.setOnClickListener {
            val item = item ?: return@setOnClickListener
            debugLog { "isgift ${item.isGift} isBottle ${item.isBottle}" }
            if (item.isGift) return@setOnClickListener

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
    }

    override fun bind(item: ProductUI) {
        super.bind(item)

        binding.imgFavoriteStatus.visibility = View.VISIBLE
        binding.cgStatuses.visibility = View.VISIBLE
        binding.clPricesContainer.visibility = View.VISIBLE
        binding.tvOldPrice.visibility = View.VISIBLE
        binding.amountController.root.visibility = View.VISIBLE

        binding.tvName.text = item.name
        binding.rbRating.rating = item.rating
        binding.llRatingContainer.visibility = View.VISIBLE

        binding.rbRating.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { p0, newRating, p2 ->
                if (newRating != binding.rbRating.rating) {
                    productsClickListener.onChangeRating(item.id, newRating, item.rating)
                }
            }

        binding.rbRating.isVisible = !item.isBottle

        binding.amountController.add.isSelected = false

        if (item.pricePerUnit != 0) {
            binding.tvPricePerUnit.visibility = View.VISIBLE
            binding.tvPricePerUnit.setPricePerUnitText(item.pricePerUnit)
        } else if (item.orderQuantity != 0) {
            binding.tvPricePerUnit.visibility = View.VISIBLE
            binding.tvPricePerUnit.setOrderQuantity(item.orderQuantity)
        } else {
            binding.tvPricePerUnit.visibility = View.GONE
        }

        var haveDiscount = false
        when(item.priceList.size) {
            1 -> {
                binding.tvPrice.setPriceText(item.priceList.first().currentPrice, itCanBeGift = true)
                binding.tvOldPrice.setPriceText(item.priceList.first().oldPrice)
                binding.tvPriceCondition.visibility = View.GONE
                if (item.priceList.first().currentPrice < item.priceList.first().oldPrice || item.isGift) haveDiscount = true
            }
            else -> {
                val minimalPrice = item.priceList.sortedBy { it.requiredAmount }.find { it.requiredAmount >= item.cartQuantity }
                minimalPrice?.let {
                    binding.tvPrice.setPriceText(minimalPrice.currentPrice)
                    binding.tvPriceCondition.visibility = View.GONE
                }
                binding.tvPricePerUnit.visibility = View.GONE
            }
        }

        when(haveDiscount) {
            true -> {
                binding.tvPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                binding.tvOldPrice.visibility = View.VISIBLE
            }
            false -> {
                binding.tvPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_new_black))
                binding.tvOldPrice.visibility = View.GONE
            }
        }

        binding.amountController.circleAmount.text = item.cartQuantity.toString()
        binding.amountController.amount.text = item.cartQuantity.toString()

        when (item.cartQuantity > 0) {
            true -> binding.amountController.circleAmount.visibility = View.VISIBLE
            false -> binding.amountController.circleAmount.visibility = View.GONE
        }

        when (item.commentAmount.isEmpty()) {
            true -> binding.tvCommentAmount.visibility = View.GONE
            else -> {
                binding.tvCommentAmount.visibility = View.VISIBLE
                binding.tvCommentAmount.text = item.commentAmount
            }
        }

        bindFav(item)

        var isNotHaveStatuses = true
        when (item.status.isEmpty()) {
            true -> binding.cwStatusContainer.visibility = View.GONE
            false -> {
                isNotHaveStatuses = false
                binding.cwStatusContainer.visibility = View.VISIBLE
                binding.tvStatus.text = item.status
                binding.cwStatusContainer.setCardBackgroundColor(Color.parseColor(item.statusColor))
            }
        }

        when(item.depositPrice != 0) {
            true -> {
                binding.tvDepositPrice.visibility = View.VISIBLE
                binding.tvDepositPrice.setDepositPriceText(item.depositPrice)
            }
            false -> binding.tvDepositPrice.visibility = View.GONE
        }

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
        binding.tlIndicators.isVisible = item.detailPictureList.size != 1

        detailPictureFlowPagerAdapter.submitList(item.detailPictureList.map { DetailPicturePager(it) })

        //If is bottle
        if (item.isBottle) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.cgStatuses.visibility = View.GONE
            binding.clPricesContainer.visibility = View.GONE
            binding.tvDepositPrice.visibility = View.VISIBLE
            binding.tvDepositPrice.setDepositPriceText(item.priceList.first().currentPrice)
            /*if (item.priceList.first().currentPrice > 0) {
                binding.clPricesContainer.visibility = View.VISIBLE
            } else {
                binding.clPricesContainer.visibility = View.GONE
            }*/ //todo
        }

        //If is gift
        if (item.priceList.first().currentPrice <= 1) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.cgStatuses.visibility = View.GONE
            binding.tvOldPrice.visibility = View.GONE
        }

        //If is not available
        if (!item.isAvailable) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.amountController.root.visibility = View.INVISIBLE
        }

    }

    private fun bindFav(item: ProductUI) {
        when(item.isFavorite) {
            false -> binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_favorite_black))
            true -> binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_ic_favorite_red))
        }
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