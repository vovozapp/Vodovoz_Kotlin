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
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setLimitedText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setOrderQuantity
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.view.LabelChip
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
        binding.vpPictures.setOnClickListener {
            val item = item ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }

        binding.vpPictures.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.llPricesContainer.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.amountController.intoCartButton.isAllCaps = false

        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }

        binding.vpPictures.setOnClickListener {
            val item = item ?: return@setOnClickListener
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

        binding.amountController.intoCartButton.setOnClickListener {
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

        RatingBar.OnRatingBarChangeListener { _, newRating, _ ->
            val item = item ?: return@OnRatingBarChangeListener
            if (newRating != binding.rbRating.rating) {
                productsClickListener.onChangeRating(item.id, newRating, item.rating)
            }
        }.also { binding.rbRating.onRatingBarChangeListener = it }
    }

    override fun bind(item: ProductUI) {
        super.bind(item)

        bindFav(item)

        showAmountController(item)

        binding.tvName.setLimitedText(item.name)
        binding.rbRating.rating = item.rating

        //Price per unit / or order quantity
        when (item.pricePerUnit.isNotEmpty()) {
            true -> {
                binding.llPricesContainer.tvPricePerUnit.visibility = View.VISIBLE
                binding.llPricesContainer.tvPricePerUnit.text = item.pricePerUnit
            }

            false -> binding.llPricesContainer.tvPricePerUnit.visibility = View.GONE
        }

        //Price
        when (item.priceList.size) {
            1 -> {
                binding.llPricesContainer.tvCurrentPrice.text = item.currentPriceStringBuilder
                binding.llPricesContainer.tvOldPrice.text = item.oldPriceStringBuilder
                binding.llPricesContainer.tvPriceCondition.visibility = View.GONE
            }

            else -> {
                if (item.conditionPrice.isNotEmpty()) {
                    binding.llPricesContainer.tvCurrentPrice.text = item.conditionPrice
                    if (item.condition.isNotEmpty()) {
                        binding.llPricesContainer.tvPriceCondition.text = item.condition
                        binding.llPricesContainer.tvPriceCondition.visibility = View.VISIBLE
                    } else {
                        binding.llPricesContainer.tvPricePerUnit.visibility = View.GONE
                    }
                } else {
                    binding.llPricesContainer.tvCurrentPrice.text = item.minimalPriceStringBuilder
                    binding.llPricesContainer.tvPriceCondition.text =
                        item.priceConditionStringBuilder
                    binding.llPricesContainer.tvPriceCondition.visibility = View.VISIBLE
                    binding.llPricesContainer.tvPricePerUnit.visibility = View.GONE
                }

            }
        }

        if (item.haveDiscount) {
            binding.llPricesContainer.tvOldPrice.visibility = View.VISIBLE
        } else {
            binding.llPricesContainer.tvOldPrice.visibility = View.GONE
        }

        //Cart amount
        binding.amountController.amount.text = item.cartQuantity.toString()

        //Comment
        when (item.rating.toString().isEmpty() || item.rating <= 0.0) {
            true -> binding.tvRating.visibility = View.GONE
            else -> {
                binding.tvRating.visibility = View.VISIBLE
                binding.tvRating.text = item.rating.toString()
            }
        }
        var isNotHaveStatuses = true
        if(item.labels.isNotEmpty()){
            //binding.chipGroup.removeAllViews()
            //binding.chipGroup.visibility = View.VISIBLE
            //for (label in item.labels) {
            //    val chip = LabelChip(binding.root.context)
            //    chip.text = label.name
            //    chip.color = Color.parseColor(label.color)
            //    binding.chipGroup.addView(chip)
            //}
        } else {

            //Status
            //binding.chipGroup.visibility = View.GONE
            // when (item.status.isEmpty()) {
            //     true -> binding.cgStatuses.cwStatusContainer.visibility = View.GONE
            //     false -> {
            //         isNotHaveStatuses = false
            //         binding.cgStatuses.cwStatusContainer.visibility = View.VISIBLE
            //         binding.cgStatuses.tvStatus.text = item.status
            //         binding.cgStatuses.cwStatusContainer.setCardBackgroundColor(
            //             Color.parseColor(
            //                 item.statusColor
            //             )
            //         )
            //     }
            // }

            //DiscountPercent
            when (item.priceList.size == 1 &&
                    item.priceList.first().currentPrice < item.priceList.first().oldPrice) {
                true -> {
                    isNotHaveStatuses = false
                    binding.cwDiscountContainer.visibility = View.VISIBLE
                    binding.tvDiscountPercent.text = item.discountPercentStringBuilder
                }

                false -> binding.cgStatuses.visibility = View.GONE
            }
        }

        when (isNotHaveStatuses) {
            true -> binding.cgStatuses.visibility = View.GONE
            false -> binding.cgStatuses.visibility = View.VISIBLE
        }

        //UpdatePictures

        val detailPictureFlowPagerAdapter = DetailPictureFlowPagerAdapter(
            clickListener = object : DetailPictureFlowClickListener {
                override fun onDetailPictureClick() {
                    productsClickListener.onProductClick(item.id)
                }

                override fun onProductClick(id: Long) {
                    productsClickListener.onProductClick(item.id)
                }
            }
        )
        detailPictureFlowPagerAdapter.submitList(item.detailPictureListPager)
        binding.vpPictures.adapter = detailPictureFlowPagerAdapter
    }

    private fun bindFav(item: ProductUI) {
        binding.imgFavoriteStatus.isSelected = item.isFavorite
    }

    private fun showAmountController(item: ProductUI?) {
        if (item != null) {
            if (item.cartQuantity > 0) {
                binding.amountController.intoCartButton.visibility = View.INVISIBLE
                binding.amountController.amountControllerDeployed.visibility = View.VISIBLE
            }
            else{
                binding.amountController.intoCartButton.visibility = View.VISIBLE
                binding.amountController.amountControllerDeployed.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateCartQuantity(item: ProductUI) {
        if (item.cartQuantity < 0) {
            item.cartQuantity = 0
        }
        binding.amountController.amount.text = item.cartQuantity.toString()
        showAmountController(item)
    }

    private fun onChangeProductQuantity(item: ProductUI){
        productsClickListener.onChangeProductQuantity(
            item.id,
            item.cartQuantity,
            item.oldQuantity
        )
    }
}