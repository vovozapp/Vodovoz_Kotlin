package com.vodovoz.app.feature.productlist.viewholders

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
import com.vodovoz.app.core.network.ApiConfig.AMOUNT_CONTROLLER_TIMER
import com.vodovoz.app.databinding.ViewHolderProductGridBinding
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowClickListener
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowPagerAdapter
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductsGridViewHolder(
    view: View,
    val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager,
    private val cartManager: CartManager
) : ItemViewHolder<ProductUI>(view) {

    private val binding: ViewHolderProductGridBinding = ViewHolderProductGridBinding.bind(view)

    private val amountControllerTimer = object : CountDownTimer(AMOUNT_CONTROLLER_TIMER, AMOUNT_CONTROLLER_TIMER) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            val item = getItemByPosition() ?: return
            productsClickListener.onChangeProductQuantity(item.id, item.cartQuantity, item.oldQuantity)
            hideAmountController(item)
        }
    }

    private val detailPictureFlowPagerAdapter = DetailPictureFlowPagerAdapter(
        clickListener = object : DetailPictureFlowClickListener {
            override fun onDetailPictureClick() {

            }

            override fun onProductClick() {
                val item = getItemByPosition() ?: return
                productsClickListener.onProductClick(item.id)
            }
        }
    )

    override fun attach() {
        super.attach()

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
            val item = getItemByPosition() ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }
        binding.pvPictures.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.pvPictures.adapter = detailPictureFlowPagerAdapter

        TabLayoutMediator(binding.tlIndicators, binding.pvPictures) { _, _ -> }.attach()

        binding.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }

        binding.pvPictures.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }

        binding.amountController.add.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
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
            val item = getItemByPosition() ?: return@setOnClickListener
            item.oldQuantity = item.cartQuantity
            item.cartQuantity--
            if (item.cartQuantity < 0) item.cartQuantity = 0
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity(item)
        }

        binding.amountController.increaseAmount.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            item.oldQuantity = item.cartQuantity
            item.cartQuantity++
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity(item)
        }

        binding.imgFavoriteStatus.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            when(item.isFavorite) {
                true -> {
                    item.isFavorite = false
                    binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_favorite_black))
                }
                false -> {
                    item.isFavorite = true
                    binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_ic_favorite_red))
                }
            }
            productsClickListener.onFavoriteClick(item.id, item.isFavorite)
        }
    }

    override fun bind(item: ProductUI) {
        super.bind(item)

        bindFav(item)

        binding.tvName.setLimitedText(item.name)
        binding.rbRating.rating = item.rating.toFloat()

        //If left items = 0
        when(item.leftItems == 0) {
            true -> {
                binding.amountController.add.setBackgroundResource(R.drawable.bkg_button_gray_circle_normal)
                binding.amountController.add.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_alert))
            }
            false -> {
                binding.amountController.add.setBackgroundResource(R.drawable.bkg_button_green_circle_normal)
                binding.amountController.add.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_cart))
            }
        }

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
                binding.tvPriceCondition.visibility = View.GONE
                if (item.priceList.first().currentPrice < item.priceList.first().oldPrice || item.isGift) haveDiscount = true
            }
            else -> {
                val minimalPrice = item.priceList.maxByOrNull { it.requiredAmount }!!
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
                binding.tvCurrentPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_new_black))
                binding.tvOldPrice.visibility = View.GONE
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
            true -> binding.rlStatusContainer.visibility = View.GONE
            false -> {
                isNotHaveStatuses = false
                binding.rlStatusContainer.visibility = View.VISIBLE
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
        binding.tlIndicators.isVisible = item.detailPictureList.size != 1

        val diffUtil = DetailPictureDiffUtilCallback(
            oldList = detailPictureFlowPagerAdapter.detailPictureUrlList,
            newList = item.detailPictureList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            detailPictureFlowPagerAdapter.detailPictureUrlList = item.detailPictureList
            diffResult.dispatchUpdatesTo(detailPictureFlowPagerAdapter)
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

    private fun getItemByPosition(): ProductUI? {
        return (bindingAdapter as? SortedAdapter)?.getItem(bindingAdapterPosition) as? ProductUI
    }
}