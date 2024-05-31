package com.vodovoz.app.feature.productlist.viewholders

import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.View
import android.widget.RatingBar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
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
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDepositPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setOrderQuantity
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ProdListViewHolder(
    view: View,
    val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager,
    private val cartManager: CartManager,
    private val ratingProductManager: RatingProductManager,
) : ItemViewHolder<ProductUI>(view) {

    private val binding: ViewHolderProductListBinding = ViewHolderProductListBinding.bind(view)

    private val amountControllerTimer =
        object : CountDownTimer(AMOUNT_CONTROLLER_TIMER, AMOUNT_CONTROLLER_TIMER) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                val item = item ?: return
                productsClickListener.onChangeProductQuantity(
                    item.id,
                    item.cartQuantity,
                    item.oldQuantity
                )
                hideAmountController(item)
            }
        }

    override fun attach() {
        super.attach()

        launch {
            val item = item ?: return@launch
            ratingProductManager
                .observeRatings()
                .filter { it.containsKey(item.id) }
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
                    hideAmountController(item)
                }
                .collect()
        }
    }

    init {
        binding.vpPictures.setOnClickListener {
            val item = item ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }
        binding.vpPictures.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.llPricesContainer.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }

        binding.vpPictures.setOnClickListener {
            val item = item ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }

        binding.amountController.add.setOnClickListener {
            val item = item ?: return@setOnClickListener
            if (item.isGift) return@setOnClickListener
            if (item.leftItems == 0) {
                productsClickListener.onNotifyWhenBeAvailable(
                    item.id,
                    item.name,
                    item.detailPicture
                )
                return@setOnClickListener
            }

            if (item.cartQuantity == 0) {
                item.cartQuantity++
            }
            updateCartQuantity(item)
            showAmountController()
        }

        binding.amountController.reduceAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
            item.cartQuantity--
            if (item.cartQuantity < 0) item.cartQuantity = 0
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity(item)
        }

        binding.amountController.increaseAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
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
            productsClickListener.onFavoriteClick(item.id, item.isFavorite)
        }
    }

    override fun bind(item: ProductUI) {
        super.bind(item)

        binding.imgFavoriteStatus.visibility = View.VISIBLE
        binding.cgStatuses.visibility = View.VISIBLE
        binding.llPricesContainer.root.visibility = View.VISIBLE
        binding.llPricesContainer.tvOldPrice.visibility = View.VISIBLE
        binding.amountController.root.visibility = View.VISIBLE

        binding.tvName.text = item.name
        binding.rbRating.rating = item.rating

        binding.rbRating.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, newRating, _ ->
                if (newRating != binding.rbRating.rating) {
                    productsClickListener.onChangeRating(item.id, newRating, item.rating)
                }
            }


        //If left items = 0
        binding.amountController.add.isSelected = item.leftItems == 0

        if (item.pricePerUnit.isNotEmpty()) {
            binding.llPricesContainer.tvPricePerUnit.visibility = View.VISIBLE
            binding.llPricesContainer.tvPricePerUnit.text = item.pricePerUnit
        } else if (item.orderQuantity != 0) {
            binding.llPricesContainer.tvPricePerUnit.visibility = View.VISIBLE
            binding.llPricesContainer.tvPricePerUnit.setOrderQuantity(item.orderQuantity)
        } else {
            binding.llPricesContainer.tvPricePerUnit.visibility = View.GONE
        }

        var haveDiscount = false
        when (item.priceList.size) {
            1 -> {
                binding.llPricesContainer.tvCurrentPrice.setPriceText(
                    item.priceList.first().currentPrice.roundToInt(),
                    itCanBeGift = true
                )
                binding.llPricesContainer.tvOldPrice.setPriceText(item.priceList.first().oldPrice.roundToInt())
                binding.llPricesContainer.tvPriceCondition.visibility = View.GONE
                if (item.priceList.first().currentPrice < item.priceList.first().oldPrice || item.isGift) haveDiscount =
                    true
            }

            else -> {
                if(item.conditionPrice.isNotEmpty()){
                    binding.llPricesContainer.tvCurrentPrice.text = item.conditionPrice
                    if(item.condition.isNotEmpty()){
                        binding.llPricesContainer.tvPriceCondition.text = item.condition
                        binding.llPricesContainer.tvPriceCondition.visibility = View.VISIBLE
                    } else {
                        binding.llPricesContainer.tvPricePerUnit.visibility = View.GONE
                    }
                } else {
                    val minimalPrice = item.priceList.sortedBy { it.requiredAmount }
                        .find { it.requiredAmount >= item.cartQuantity }
                    minimalPrice?.let {
                        binding.llPricesContainer.tvCurrentPrice.setPriceText(minimalPrice.currentPrice.roundToInt())
                        binding.llPricesContainer.tvPriceCondition.visibility = View.GONE
                    }
                    binding.llPricesContainer.tvPricePerUnit.visibility = View.GONE
                }

            }
        }

        if (haveDiscount) {
            binding.llPricesContainer.tvCurrentPrice.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.red
                )
            )
            binding.llPricesContainer.tvOldPrice.visibility = View.VISIBLE
        } else {
            binding.llPricesContainer.tvCurrentPrice.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.text_black
                )
            )
            binding.llPricesContainer.tvOldPrice.visibility = View.GONE
        }

        updateCartQuantity(item)

        if (item.commentAmount.isEmpty()) {
            binding.tvCommentAmount.visibility = View.GONE
        } else {
            binding.tvCommentAmount.visibility = View.VISIBLE
            binding.tvCommentAmount.text = item.commentAmount
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

        when (item.depositPrice != 0) {
            true -> {
                binding.tvDepositPrice.visibility = View.VISIBLE
                binding.tvDepositPrice.setDepositPriceText(item.depositPrice)
            }

            false -> binding.tvDepositPrice.visibility = View.GONE
        }

        when (item.priceList.size == 1 &&
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

        when (isNotHaveStatuses) {
            true -> binding.cgStatuses.visibility = View.GONE
            false -> binding.cgStatuses.visibility = View.VISIBLE
        }

        binding.tlIndicators.visibility = if(item.detailPictureList.size != 1){
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

        val detailPictureFlowPagerAdapter = DetailPictureFlowPagerAdapter(
            clickListener = object : DetailPictureFlowClickListener {
                override fun onProductClick(id: Long) {
                    productsClickListener.onProductClick(item.id)
                }

                override fun onDetailPictureClick() {

                }
            }
        )
        binding.vpPictures.adapter = detailPictureFlowPagerAdapter
        detailPictureFlowPagerAdapter.submitList(item.detailPictureList.map { DetailPicturePager(it) })
        binding.tlIndicators.attachToPager(binding.vpPictures)

        //If is bottle
        if (item.isBottle) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.cgStatuses.visibility = View.GONE
            binding.llPricesContainer.root.visibility = View.GONE
        }

        //If is gift
        if (item.isGift) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.cgStatuses.visibility = View.GONE
            binding.llPricesContainer.tvOldPrice.visibility = View.GONE
        }

        //If is not available
        if (!item.isAvailable) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.amountController.root.visibility = View.INVISIBLE
        }

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

    internal fun hideAmountController(item: ProductUI) {
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
        binding.amountController.circleAmount.visibility = if (item.cartQuantity > 0 && !binding.amountController.amountControllerDeployed.isVisible) {
            View.VISIBLE
        } else {
            View.GONE
        }
        binding.amountController.amount.text = item.cartQuantity.toString()
        binding.amountController.circleAmount.text = item.cartQuantity.toString()
    }
}