package com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner

import android.graphics.Color
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderProductListBinding
import com.vodovoz.app.feature.cart.adapter.CartMainAdapter
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowClickListener
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowPagerAdapter
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.ui.adapter.DetailPicturePagerAdapter
import com.vodovoz.app.ui.diffUtils.DetailPictureDiffUtilCallback
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDepositPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setOrderQuantity
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPricePerUnitText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI

class AvailableProductsViewHolder(
    view: View,
    val clickListener: CartMainClickListener,
    val productsClickListener: ProductsClickListener
) : ItemViewHolder<ProductUI>(view) {

    private val binding: ViewHolderProductListBinding = ViewHolderProductListBinding.bind(view)

    private val amountControllerTimer = object : CountDownTimer(3000, 3000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            val item = getItemByPosition() ?: return
            productsClickListener.onChangeProductQuantity(item.id, item.cartQuantity)
            hideAmountController(item)
        }
    }

    private val detailPictureFlowPagerAdapter = DetailPictureFlowPagerAdapter(
        clickListener = object : DetailPictureFlowClickListener {
            override fun onProductClick() {
                val item = getItemByPosition() ?: return
                productsClickListener.onProductClick(item.id)
            }
        }
    )

    init {
        binding.vpPictures.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }
        binding.vpPictures.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpPictures.adapter = detailPictureFlowPagerAdapter

        TabLayoutMediator(binding.tlIndicators, binding.vpPictures) { _, _ -> }.attach()

        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }

        binding.vpPictures.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }

        binding.amountController.add.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            if (item.isGift) return@setOnClickListener

            item.cartQuantity++
            updateCartQuantity(item)
            showAmountController()
        }

        binding.amountController.reduceAmount.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            item.cartQuantity--
            if (item.cartQuantity < 0) item.cartQuantity = 0
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity(item)
        }

        binding.amountController.increaseAmount.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
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

        binding.imgFavoriteStatus.visibility = View.VISIBLE
        binding.cgStatuses.visibility = View.VISIBLE
        binding.clPricesContainer.visibility = View.VISIBLE
        binding.tvOldPrice.visibility = View.VISIBLE
        binding.rlAmountControllerContainer.visibility = View.VISIBLE

        binding.tvName.text = item.name
        binding.rbRating.rating = item.rating.toFloat()

        binding.amountController.add.setBackgroundResource(R.drawable.bkg_button_green_circle_normal)
        binding.amountController.add.setImageDrawable(
            ContextCompat.getDrawable(
                itemView.context,
                R.drawable.png_cart
            )
        )

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

        binding.llRatingContainer.visibility = View.GONE

        when (item.commentAmount.isEmpty()) {
            true -> binding.tvCommentAmount.visibility = View.GONE
            else -> {
                binding.tvCommentAmount.visibility = View.VISIBLE
                binding.tvCommentAmount.text = item.commentAmount
            }
        }

        when(item.isFavorite) {
            false -> binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_favorite_black))
            true -> binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_ic_favorite_red))
        }

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

        val diffUtil = DetailPictureDiffUtilCallback(
            oldList = detailPictureFlowPagerAdapter.detailPictureUrlList,
            newList = item.detailPictureList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            detailPictureFlowPagerAdapter.detailPictureUrlList = item.detailPictureList
            diffResult.dispatchUpdatesTo(detailPictureFlowPagerAdapter)
        }

        //If is bottle
        if (item.isBottle) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.cgStatuses.visibility = View.GONE
            binding.clPricesContainer.visibility = View.GONE
        }

        //If is gift
        if (item.isGift) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.cgStatuses.visibility = View.GONE
            if (item.isGift) binding.tvOldPrice.visibility = View.GONE
        }

        //If is not available
        if (!item.isAvailable) {
            binding.imgFavoriteStatus.visibility = View.INVISIBLE
            binding.rlAmountControllerContainer.visibility = View.INVISIBLE
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
        return (bindingAdapter as? AvailableProductsAdapter)?.getItem(bindingAdapterPosition) as? ProductUI
    }
}