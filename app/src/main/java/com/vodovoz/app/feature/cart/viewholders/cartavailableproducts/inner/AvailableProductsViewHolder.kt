package com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner

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
                }
                .collect()
        }
    }

    init {
        binding.vpPictures.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpPictures.adapter = detailPictureFlowPagerAdapter

        binding.tlIndicators.attachTo(binding.vpPictures)

        binding.llPricesContainer.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

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
            if (item.isGift || item.chipsBan == 2 || item.chipsBan == 3 || item.chipsBan == 5) return@setOnClickListener

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
        binding.llRatingContainer.visibility = View.VISIBLE

        binding.rbRating.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, newRating, _ ->
                if (newRating != binding.rbRating.rating) {
                    productsClickListener.onChangeRating(item.id, newRating, item.rating)
                }
            }

        binding.amountController.add.isSelected = false

        if (item.pricePerUnit.isNotEmpty()) {
            binding.llPricesContainer.tvPricePerUnit.visibility = View.VISIBLE
            binding.llPricesContainer.tvPricePerUnit.text = item.pricePerUnit
        } else if (item.orderQuantity != 0) {
            binding.llPricesContainer.tvPricePerUnit.visibility = View.VISIBLE
            binding.llPricesContainer.tvPricePerUnit.setOrderQuantity(item.orderQuantity)
        } else {
            binding.llPricesContainer.tvPricePerUnit.visibility = View.GONE
        }

        updateCartQuantity(item)

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

        when (item.depositPrice != 0) {
            true -> {
                binding.tvDepositPrice.visibility = View.VISIBLE
                binding.tvDepositPrice.setDepositPriceText(item.depositPrice)
            }

            false -> binding.tvDepositPrice.visibility = View.GONE
        }

        bindDiscountAndStatuses(item)

        //UpdatePictures
        binding.tlIndicators.isVisible = item.detailPictureList.size != 1

        detailPictureFlowPagerAdapter.submitList(item.detailPictureList.map { DetailPicturePager(it) })

        //If is bottle
        if (item.isBottle) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.cgStatuses.visibility = View.GONE
            binding.llPricesContainer.root.visibility = View.GONE
            binding.tvDepositPrice.visibility = View.VISIBLE
            binding.tvDepositPrice.setDepositPriceText(item.priceList.first().currentPrice)
            binding.rbRating.isVisible = false
            /*if (item.priceList.first().currentPrice > 0) {
                binding.clPricesContainer.visibility = View.VISIBLE
            } else {
                binding.clPricesContainer.visibility = View.GONE
            }*/ //todo
        }

        //If is gift
        if (item.isGift) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.cgStatuses.visibility = View.GONE
            binding.llPricesContainer.tvOldPrice.visibility = View.GONE
            binding.rbRating.isVisible = false
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
        if (item.oldQuantity == 0) {
            item.oldQuantity = 1
        }
        binding.amountController.amount.text = item.cartQuantity.toString()
        binding.amountController.circleAmount.text = item.cartQuantity.toString()
        debugLog { "updateCartQuantity: ${item.oldQuantity} -> ${item.cartQuantity}" }
    }

    private fun bindDiscountAndStatuses(item: ProductUI) {

        if (item.status.isEmpty()) {
            binding.cwStatusContainer.visibility = View.GONE
        } else {
            binding.cwStatusContainer.visibility = View.VISIBLE
            binding.tvStatus.text = item.status
            binding.cwStatusContainer.setCardBackgroundColor(Color.parseColor(item.statusColor))
        }

        when (item.priceList.size) {
            1 -> {
                binding.llPricesContainer.tvPriceCondition.visibility = View.GONE
                bindDiscountPrice(
                    item,
                    item.priceList.first().currentPrice,
                    item.priceList.first().oldPrice
                )
            }

            else -> {
                val sortedPriceList = item.priceList.sortedByDescending { it.requiredAmount }

                val minimalPrice =
                    if (sortedPriceList.first().requiredAmountTo == 0 && item.cartQuantity >= sortedPriceList.first().requiredAmount) {
                        sortedPriceList.find { it.requiredAmountTo == 0 && item.cartQuantity >= it.requiredAmount }
                    } else {
                        sortedPriceList.find { item.cartQuantity in it.requiredAmount..it.requiredAmountTo }
                    } ?: sortedPriceList.first()

                binding.llPricesContainer.tvPriceCondition.visibility = View.VISIBLE
                binding.llPricesContainer.tvPriceCondition.text =
                    itemView.context.getString(
                        R.string.price_condition,
                        minimalPrice.requiredAmount
                    )
//                    "При условии покупки от ${minimalPrice.requiredAmount} шт"

                bindDiscountPrice(item, minimalPrice.currentPrice, minimalPrice.oldPrice)
                binding.llPricesContainer.tvPricePerUnit.visibility = View.GONE
            }
        }

        if (item.totalDisc > 0 || item.isGift) {
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
    }

    private fun bindDiscountPrice(item: ProductUI, currentPrice: Int, oldPrice: Int = 0) {
        if (item.totalDisc > 0) {

            if (oldPrice == 0) {
                val priceWithDesc = (currentPrice.toDouble() - item.totalDisc).roundToInt()
                binding.llPricesContainer.tvCurrentPrice.setPriceText(
                    priceWithDesc,
                    itCanBeGift = true
                )
                binding.llPricesContainer.tvOldPrice.setPriceText(currentPrice, itCanBeGift = true)
                binding.tvDiscountPercent.setDiscountPercent(
                    newPrice = priceWithDesc,
                    oldPrice = currentPrice
                )
            } else {
                binding.llPricesContainer.tvCurrentPrice.setPriceText(
                    currentPrice,
                    itCanBeGift = true
                )
                binding.llPricesContainer.tvOldPrice.setPriceText(oldPrice, itCanBeGift = true)
                binding.tvDiscountPercent.setDiscountPercent(
                    newPrice = currentPrice,
                    oldPrice = oldPrice
                )
            }

            binding.cwDiscountContainer.visibility = View.VISIBLE

        } else {
            binding.llPricesContainer.tvCurrentPrice.setPriceText(currentPrice, itCanBeGift = true)
            binding.llPricesContainer.tvOldPrice.isVisible = false

            if (item.status.isNotEmpty()) {
                binding.cwDiscountContainer.visibility = View.GONE
            } else {
                binding.cgStatuses.visibility = View.GONE
            }
        }
    }
}