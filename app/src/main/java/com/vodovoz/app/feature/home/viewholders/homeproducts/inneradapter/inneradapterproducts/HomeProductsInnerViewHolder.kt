package com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts

import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.core.network.ApiConfig.AMOUNT_CONTROLLER_TIMER
import com.vodovoz.app.databinding.ViewHolderSliderProductBinding
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowClickListener
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowPagerAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setLimitedText
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.view.LabelChip
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeProductsInnerViewHolder(
    view: View,
    private val clickListener: ProductsClickListener,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
) : ItemViewHolder<ProductUI>(view) {

    private val binding: ViewHolderSliderProductBinding = ViewHolderSliderProductBinding.bind(view)

    override fun attach() {
        super.attach()

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
    }

    init {
        binding.llPricesContainer.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onProductClick(item.id)
        }
        binding.amountController.intoCartButton.isAllCaps = false

        binding.amountController.reduceAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
//            item.oldQuantity = item.cartQuantity
            item.cartQuantity--
            if (item.cartQuantity < 0) item.cartQuantity = 0
            updateCartQuantity(item)
            onChangeProductQuantity(item)
        }

        binding.amountController.increaseAmount.setOnClickListener {
            val item = item ?: return@setOnClickListener
//            item.oldQuantity = item.cartQuantity
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
            clickListener.onFavoriteClick(item.id, item.isFavorite)
        }
    }

    override fun bind(item: ProductUI) {
        super.bind(item)
        binding.llPricesContainer.tvPriceCondition.visibility = View.GONE
        binding.tvName.setLimitedText(item.name)
        binding.rbRating.rating = item.rating

        //Price per unit / or order quantity
        binding.llPricesContainer.tvPricePerUnit.isVisible = item.pricePerUnit.isNotEmpty()
        binding.llPricesContainer.tvPricePerUnit.text = item.pricePerUnit

        //Price
        when (item.priceList.size) {
            1 -> {
                binding.llPricesContainer.tvCurrentPrice.text = item.currentPriceStringBuilder
                binding.llPricesContainer.tvOldPrice.text = item.oldPriceStringBuilder
            }

            else -> {
                if (item.conditionPrice.isNotEmpty()) {
                    binding.llPricesContainer.tvCurrentPrice.text = item.conditionPrice
                } else {
                    binding.llPricesContainer.tvCurrentPrice.text = item.minimalPriceStringBuilder
                    binding.llPricesContainer.tvPricePerUnit.visibility = View.GONE
                }
            }
        }
        when (item.haveDiscount) {
            true -> {
                binding.llPricesContainer.tvOldPrice.visibility = View.VISIBLE
            }

            false -> {
                binding.llPricesContainer.tvOldPrice.visibility = View.GONE
            }
        }

        //Cart amount
        updateCartQuantity(item)

        //Favorite
        bindFav(item)


        //Status
        binding.cgStatuses.root.visibility = View.GONE
        if(item.labels.isNotEmpty()){
            binding.chipGroup.removeAllViews()
            binding.chipGroup.visibility = View.VISIBLE
            for (label in item.labels) {
                if (!label.name.contains("%")){
                    binding.cgStatuses.root.visibility = View.GONE
                    val chip = LabelChip(binding.root.context)
                    chip.text = label.name
                    chip.color = Color.parseColor(label.color)
                    binding.chipGroup.addView(chip)
                }
                else{
                    binding.cgStatuses.root.visibility = View.VISIBLE
                    binding.cgStatuses.tvDiscountPercent.visibility = View.VISIBLE
                    binding.cgStatuses.tvDiscountPercent.text = label.name
                }
            }
        } else {
            binding.chipGroup.visibility = View.GONE
            // when (item.status.isEmpty()) {
            //     true -> binding.cgStatuses.cwStatusContainer.visibility = View.GONE
            //     false -> {
            //         binding.cgStatuses.cwStatusContainer.visibility = View.VISIBLE
            //         binding.cgStatuses.tvStatus.text = item.status
            //         binding.cgStatuses.cwStatusContainer.setCardBackgroundColor(
            //             Color.parseColor(
            //                 item.statusColor
            //             )
            //         )
            //     }
            // }


        }

        //DiscountPercent

        //UpdatePictures
        val detailPictureFlowPagerAdapter = DetailPictureFlowPagerAdapter(
            clickListener = object : DetailPictureFlowClickListener {
                override fun onDetailPictureClick() {
                    clickListener.onProductClick(item.id)
                }

                override fun onProductClick(id: Long) {
                    clickListener.onProductClick(item.id)
                }
            }
        )
        detailPictureFlowPagerAdapter.submitList(item.detailPictureListPager)
        binding.pvPictures.adapter = detailPictureFlowPagerAdapter
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
        clickListener.onChangeProductQuantity(
            item.id,
            item.cartQuantity,
            item.oldQuantity
        )
    }
}