package com.vodovoz.app.feature.productdetail.viewholders.detailheader

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentProductDetailsHeaderBinding
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowClickListener
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowPagerAdapter
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.ui.diffUtils.DetailPictureDiffUtilCallback
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setCommentQuantity
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceCondition
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPricePerUnitText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished
import com.vodovoz.app.ui.model.ProductDetailUI
import com.vodovoz.app.ui.model.ProductUI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DetailHeaderViewHolder(
    view: View,
    val clickListener: ProductDetailsClickListener,
    private val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager,
    private val cartManager: CartManager
) : ItemViewHolder<DetailHeader>(view) {

    private val binding: FragmentProductDetailsHeaderBinding = FragmentProductDetailsHeaderBinding.bind(view)

    private val detailPictureFlowPagerAdapter = DetailPictureFlowPagerAdapter(
        clickListener = object : DetailPictureFlowClickListener {
            override fun onProductClick() {
                val item = item?.productDetailUI ?: return
                productsClickListener.onProductClick(item.id)
            }
        }
    )

    private val amountControllerTimer = object : CountDownTimer(3000, 3000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            val item = item?.productDetailUI ?: return
            productsClickListener.onChangeProductQuantity(
                id = item.id,
                cartQuantity = item.cartQuantity,
                oldQuantity = item.oldQuantity
            )
            hideAmountController(item)
        }
    }

    init {

        launch {
            likeManager
                .observeLikes()
                .filter{ it.containsKey(item?.productDetailUI?.id ?: 0) }
                .onEach {
                    val item = item?.productDetailUI
                    if (item != null) {
                        item.isFavorite = it[item.id] ?: item.isFavorite
                        bindFav(item)
                    }
                }
                .collect()
        }

        launch {
            cartManager
                .observeCarts()
                .filter { it.containsKey(item?.productDetailUI?.id) }
                .onEach {
                    val item = item?.productDetailUI
                    if (item != null) {
                        item.cartQuantity = it[item.id] ?: item.cartQuantity
                        updateCartQuantity(item)
                    }
                }
                .collect()
        }

        binding.imgBack.setOnClickListener { clickListener.backPress() }
        binding.vpPictures.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpPictures.adapter = detailPictureFlowPagerAdapter
        TabLayoutMediator(binding.tlIndicators, binding.vpPictures) { _, _ -> }.attach()

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
            when(item.isFavorite) {
                true -> {
                    item.isFavorite = false
                    binding.imgFavorite.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_favorite_black))
                }
                false -> {
                    item.isFavorite = true
                    binding.imgFavorite.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_ic_favorite_red))
                }
            }
            productsClickListener.onFavoriteClick(item.id, item.isFavorite)
        }

        binding.amountController.add.setOnClickListener {
            val item = item ?: return@setOnClickListener
            add(item)
        }
        binding.amountController.reduceAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
            reduceAmount(item)
        }
        binding.amountController.increaseAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
            increaseAmount(item)
        }
    }

    @SuppressLint("Range")
    override fun bind(item: DetailHeader) {
        super.bind(item)

        binding.tlIndicators.isVisible = item.productDetailUI.detailPictureList.size != 1

        item.productDetailUI.brandUI?.let { binding.tvBrand.text = it.name }

        binding.tvName.text = item.productDetailUI.name
        binding.rbRating.rating = item.productDetailUI.rating.toFloat()

        when(item.productDetailUI.youtubeVideoCode.isEmpty()) {
            true -> binding.cwPlayVideo.visibility = View.GONE
            false -> binding.cwPlayVideo.visibility = View.VISIBLE
        }

        //If left items = 0
        when(item.productDetailUI.leftItems == 0) {
            true -> {
                when(item.replacementProductsCategoryDetail?.productUIList?.isEmpty()) {
                    true -> {
                        clickListener.showFabBell()
                        binding.amountController.add.setBackgroundResource(R.drawable.bkg_button_gray_circle_normal)
                        binding.amountController.add.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_alert))
                    }
                    false -> {
                        clickListener.showFabReplace()
                        binding.amountController.add.setBackgroundResource(R.drawable.bkg_button_orange_circle_normal)
                        binding.amountController.add.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_swap))
                    }
                    else -> {}
                }
            }
            false -> {
                clickListener.showFabBasket()
                binding.amountController.add.setBackgroundResource(R.drawable.bkg_button_green_circle_normal)
                binding.amountController.add.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_cart))
            }
        }

        //Price per unit / or order quantity
        when(item.productDetailUI.pricePerUnit != 0) {
            true -> {
                binding.tvPricePerUnit.visibility = View.VISIBLE
                binding.tvPricePerUnit.setPricePerUnitText(item.productDetailUI.pricePerUnit)
            }
            false -> binding.tvPricePerUnit.visibility = View.GONE
        }

        //Price
        var haveDiscount = false
        when(item.productDetailUI.priceUIList.size) {
            1 -> {
                binding.tvCurrentPrice.setPriceText(item.productDetailUI.priceUIList.first().currentPrice)
                binding.tvOldPrice.setPriceText(item.productDetailUI.priceUIList.first().oldPrice)
                binding.tvPriceCondition.visibility = View.GONE
                if (item.productDetailUI.priceUIList.first().currentPrice <
                    item.productDetailUI.priceUIList.first().oldPrice) haveDiscount = true
            }
            else -> {
                val minimalPrice = item.productDetailUI.priceUIList.maxByOrNull { it.requiredAmount }!!
                binding.tvCurrentPrice.setMinimalPriceText(minimalPrice.currentPrice)
                binding.tvPriceCondition.setPriceCondition(minimalPrice.requiredAmount)
                binding.tvPriceCondition.visibility = View.VISIBLE
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
        binding.amountController.circleAmount.text = item.productDetailUI.cartQuantity.toString()
        binding.amountController.amount.text = item.productDetailUI.cartQuantity.toString()

        when (item.productDetailUI.cartQuantity > 0) {
            true -> {
                binding.amountController.circleAmount.visibility = View.VISIBLE
            }
            false -> {
                binding.amountController.circleAmount.visibility = View.GONE
            }
        }

        //Comment
        when (item.productDetailUI.commentsAmount == 0) {
            true -> {
                binding.tvCommentAmount.text = ""
                binding.tvCommentAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_gray))
            }
            else -> {
                binding.tvCommentAmount.setCommentQuantity(item.productDetailUI.commentsAmount)
                binding.tvCommentAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.bluePrimary))
            }
        }

        //Favorite
        bindFav(item.productDetailUI)

        //Status
        var isNotHaveStatuses = true
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
        when(item.productDetailUI.priceUIList.size == 1 &&
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

        when(isNotHaveStatuses) {
            true -> binding.cgStatuses.visibility = View.GONE
            false -> binding.cgStatuses.visibility = View.VISIBLE
        }

        val diffUtil = DetailPictureDiffUtilCallback(
            oldList = detailPictureFlowPagerAdapter.detailPictureUrlList,
            newList = item.productDetailUI.detailPictureList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            detailPictureFlowPagerAdapter.detailPictureUrlList = item.productDetailUI.detailPictureList
            diffResult.dispatchUpdatesTo(detailPictureFlowPagerAdapter)
        }

    }

    private fun bindFav(item: ProductDetailUI) {
        when(item.isFavorite) {
            false -> binding.imgFavorite.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_favorite_black))
            true -> binding.imgFavorite.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_ic_favorite_red))
        }
    }

    private fun showAmountController() {
        binding.amountController.circleAmount.visibility = View.GONE
        binding.amountController.add.visibility = View.GONE
        binding.amountController.amountControllerDeployed.visibility = View.VISIBLE
        amountControllerTimer.start()
    }

    private fun hideAmountController(item: ProductDetailUI) {
        if (item.cartQuantity > 0) {
            binding.amountController.circleAmount.visibility = View.VISIBLE
        }
        binding.amountController.add.visibility = View.VISIBLE
        binding.amountController.amountControllerDeployed.visibility = View.GONE
    }

    private fun reduceAmount(item: DetailHeader) {
        with(item.productDetailUI) {
            item.productDetailUI.oldQuantity = item.productDetailUI.cartQuantity
            cartQuantity--
            if (cartQuantity < 0) cartQuantity = 0
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity(item.productDetailUI)
        }
    }

    private fun increaseAmount(item: DetailHeader) {
        item.productDetailUI.oldQuantity = item.productDetailUI.cartQuantity
        item.productDetailUI.cartQuantity++
        amountControllerTimer.cancel()
        amountControllerTimer.start()
        updateCartQuantity(item.productDetailUI)
    }

    private fun add(item: DetailHeader) {
        if (item.productDetailUI.leftItems == 0) {
            item.replacementProductsCategoryDetail?.let {
                if (it.productUIList.isNotEmpty()) {
                    clickListener.navigateToReplacement(
                        item.productDetailUI.detailPictureList.first(),
                        it.productUIList.toTypedArray(),
                        item.productDetailUI.id,
                        item.productDetailUI.name
                    )
                }
            }
        } else {
            if (item.productDetailUI.cartQuantity == 0) {
                item.productDetailUI.oldQuantity = item.productDetailUI.cartQuantity
                item.productDetailUI.cartQuantity++
                updateCartQuantity(item.productDetailUI)
            }
            showAmountController()
        }
    }


    private fun updateCartQuantity(item: ProductDetailUI) {
        with(item) {
            if (cartQuantity < 0) {
                cartQuantity = 0
            }
            binding.amountController.amount.text = cartQuantity.toString()
            binding.amountController.circleAmount.text = cartQuantity.toString()
        }
    }

    override fun attach() {
        super.attach()

    }

    override fun detach() {
        super.detach()
        amountControllerTimer.cancel()
    }

}