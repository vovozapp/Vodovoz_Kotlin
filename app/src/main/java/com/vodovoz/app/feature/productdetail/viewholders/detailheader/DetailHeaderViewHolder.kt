package com.vodovoz.app.feature.productdetail.viewholders.detailheader

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.core.network.ApiConfig.AMOUNT_CONTROLLER_TIMER
import com.vodovoz.app.databinding.FragmentProductDetailsHeaderBinding
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowClickListener
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowPagerAdapter
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPicturePager
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceCondition
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductDetailUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.view.LabelChip
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DetailHeaderViewHolder(
    view: View,
    val clickListener: ProductDetailsClickListener,
    private val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager,
    private val cartManager: CartManager,
//    private val ratingProductManager: RatingProductManager,
) : ItemViewHolder<DetailHeader>(view) {

    private val binding: FragmentProductDetailsHeaderBinding =
        FragmentProductDetailsHeaderBinding.bind(view)

    private val detailPictureFlowPagerAdapter = DetailPictureFlowPagerAdapter(
        clickListener = object : DetailPictureFlowClickListener {

            override fun onDetailPictureClick() {
                val item = item?.productDetailUI ?: return
                clickListener.onDetailPictureClick(
                    binding.vpPictures.currentItem,
                    item.detailPictureList.toTypedArray()
                )
            }

            override fun onProductClick(id: Long) {

            }
        }
    )

    init {

//        launch {
//            val item = item?.productDetailUI ?: return@launch
//            ratingProductManager
//                .observeRatings()
//                .filter { it.containsKey(item.id) }
//                .onEach {
//                    item.rating = it[item.id] ?: item.rating
//                    binding.rbRating.rating = item.rating
//                }
//                .collect()
//        }

        launch {
            val item = item?.productDetailUI ?: return@launch
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
            val item = item?.productDetailUI ?: return@launch
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

        binding.imgBack.setOnClickListener { clickListener.backPress() }
        binding.vpPictures.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpPictures.adapter = detailPictureFlowPagerAdapter
        binding.dotsIndicator.attachTo(binding.vpPictures)

        binding.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        binding.imgShare.setOnClickListener {
            val item = item?.productDetailUI ?: return@setOnClickListener
            val intent = Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, item.shareUrl)
                },
                "Shearing Option"
            )
            clickListener.share(intent)
        }

        binding.imgFavorite.setOnClickListener {
            val item = item?.productDetailUI ?: return@setOnClickListener
            when (item.isFavorite) {
                true -> {
                    item.isFavorite = false
                    binding.imgFavorite.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_favorite_black
                        )
                    )
                }

                false -> {
                    item.isFavorite = true
                    binding.imgFavorite.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.png_ic_favorite_red
                        )
                    )
                }
            }
            productsClickListener.onFavoriteClick(item.id, item.isFavorite)
        }

        binding.amountController.reduceAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
            reduceAmount(item)
        }
        binding.amountController.increaseAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
            increaseAmount(item)
        }

        binding.amountController.intoCartButton.setOnClickListener {
            val item = item ?: return@setOnClickListener
            increaseAmount(item)
        }

        binding.llRatingContainer.setOnClickListener {
            val item = item ?: return@setOnClickListener
            if (item.productDetailUI.commentsAmount != 0) {
                clickListener.onTvCommentAmount(item.productDetailUI.id)
            }
        }

        binding.cwPlayVideo.setOnClickListener {
            val item = item ?: return@setOnClickListener
            val rutubeVideoCode = item.productDetailUI.rutubeVideoCode
            if (rutubeVideoCode.isNotEmpty()) {
                clickListener.onRuTubeClick(rutubeVideoCode)
            } else {
                val youTubeVideoCode = item.productDetailUI.youtubeVideoCode
                if (youTubeVideoCode.isNotEmpty()) {
                    clickListener.onYouTubeClick(youTubeVideoCode)
                }
            }
        }
    }

    @SuppressLint("Range")
    override fun bind(item: DetailHeader) {
        super.bind(item)

        launch {
            val product = item.productDetailUI
            cartManager
                .observeCarts()
                .filter { it.containsKey(product.id) }
                .onEach {
                    product.cartQuantity = it[product.id] ?: product.cartQuantity
                    product.oldQuantity = product.cartQuantity
                    updateCartQuantity(product)
                }
                .collect()
        }

        binding.dotsIndicator.visibility =
            if (item.productDetailUI.detailPictureList.size != 1) View.VISIBLE else View.INVISIBLE

        item.productDetailUI.brandUI?.let { binding.tvBrand.text = it.name }

        binding.tvName.text = item.productDetailUI.name
        binding.tvRatingText.text = item.productDetailUI.rating

        binding.cwPlayVideo.isVisible = item.productDetailUI.youtubeVideoCode.isNotEmpty()
                || item.productDetailUI.rutubeVideoCode.isNotEmpty()

        //If left items = 0

        //Price per unit / or order quantity
        when (item.productDetailUI.pricePerUnit.isNotEmpty()) {
            true -> {
                binding.tvPricePerUnit.visibility = View.VISIBLE
                binding.tvPricePerUnit.text = item.productDetailUI.pricePerUnit
            }
            false -> binding.tvPricePerUnit.visibility = View.GONE
        }

        //Price
        var haveDiscount = false
        when (item.productDetailUI.priceUIList.size) {
            1 -> {
                binding.tvCurrentPrice.setPriceText(item.productDetailUI.priceUIList.first().currentPrice.roundToInt())
                binding.tvOldPrice.setPriceText(item.productDetailUI.priceUIList.first().oldPrice.roundToInt())
                binding.tvPriceCondition.visibility = View.GONE
                if (item.productDetailUI.priceUIList.first().currentPrice <
                    item.productDetailUI.priceUIList.first().oldPrice
                ) haveDiscount = true
            }

            else -> {
                val minimalPrice =
                    item.productDetailUI.priceUIList.maxByOrNull { it.requiredAmount }!!
                binding.tvCurrentPrice.setMinimalPriceText(minimalPrice.currentPrice.roundToInt())
                binding.tvPriceCondition.setPriceCondition(minimalPrice.requiredAmount)
                binding.tvPriceCondition.visibility = View.VISIBLE
                binding.tvPricePerUnit.visibility = View.GONE
            }
        }
        when (haveDiscount) {
            true -> {
                binding.tvOldPrice.visibility = View.VISIBLE
            }

            false -> {
                binding.tvOldPrice.visibility = View.GONE
            }
        }

        //Cart amount
        binding.amountController.amount.text = item.productDetailUI.cartQuantity.toString()

        //Comment
        if (item.productDetailUI.commentsAmount == 0) {
            binding.tvCommentAmount.visibility = View.GONE
        } else {
            binding.tvCommentAmount.visibility = View.VISIBLE
            binding.tvCommentAmount.text = item.productDetailUI.commentsAmountText
        }

        //Favorite
        bindFav(item.productDetailUI)

        //Status
        var isNotHaveStatuses = true

        if (item.productDetailUI.labels.isNotEmpty()) {
            isNotHaveStatuses = false
            binding.cwStatusContainer.visibility = View.GONE
            binding.cwDiscountContainer.visibility = View.GONE
            binding.cgStatuses.removeAllViews()
            for (label in item.productDetailUI.labels) {
                val chip = LabelChip(binding.root.context)
                chip.text = label.name
                chip.color = Color.parseColor(label.color)
                binding.cgStatuses.addView(chip)
            }
        } else {

            when (item.productDetailUI.status.isEmpty()) {
                true -> {}
                false -> {
                    isNotHaveStatuses = false
                    binding.cwStatusContainer.visibility = View.VISIBLE
                    binding.tvStatus.text = item.productDetailUI.status
                    binding.cwStatusContainer.setCardBackgroundColor(Color.parseColor(item.productDetailUI.statusColor))
                }
            }

            //DiscountPercent
            when (item.productDetailUI.priceUIList.size == 1 &&
                    item.productDetailUI.priceUIList.first().currentPrice <
                    item.productDetailUI.priceUIList.first().oldPrice) {
                true -> {
                    binding.cwDiscountContainer.visibility = View.VISIBLE
                    binding.tvDiscountPercent.setDiscountPercent(
                        newPrice = item.productDetailUI.priceUIList.first().currentPrice,
                        oldPrice = item.productDetailUI.priceUIList.first().oldPrice
                    )
                }
                false -> binding.cwDiscountContainer.visibility = View.GONE
            }
        }

        when (isNotHaveStatuses) {
            true -> binding.cwStatusContainer.visibility = View.GONE
            false -> binding.cwStatusContainer.visibility = View.VISIBLE
        }

        detailPictureFlowPagerAdapter.submitList(item.productDetailUI.detailPictureList.map {
            DetailPicturePager(
                it
            )
        })

    }

    private fun bindFav(item: ProductDetailUI) {
        when (item.isFavorite) {
            false -> binding.imgFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.ic_favorite_black
                )
            )

            true -> binding.imgFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.png_ic_favorite_red
                )
            )
        }
    }

    private fun showAmountController(item: ProductDetailUI?) {
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

    private fun reduceAmount(item: DetailHeader) {
        with(item.productDetailUI) {
            cartQuantity--
            if (cartQuantity < 0) cartQuantity = 0
            updateCartQuantity(item.productDetailUI)
        }
    }

    private fun increaseAmount(item: DetailHeader) {
        item.productDetailUI.cartQuantity++
        updateCartQuantity(item.productDetailUI)
    }

    private fun updateCartQuantity(item: ProductDetailUI) {
        if (item.cartQuantity < 0) {
            item.cartQuantity = 0
        }
        binding.amountController.amount.text = item.cartQuantity.toString()
        showAmountController(item)
    }
}