package com.vodovoz.app.feature.cart.viewholders.cartnotavailableproducts.inner

import android.graphics.Paint
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.ViewHolderProductListNotAvailableBinding
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDepositPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceCondition
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPricePerUnitText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.ProductUI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NotAvailableProductsViewHolder(
    view: View,
    val clickListener: CartMainClickListener,
    val productsClickListener: ProductsClickListener,
    val likeManager: LikeManager
) : ItemViewHolder<ProductUI>(view) {

    private val binding: ViewHolderProductListNotAvailableBinding = ViewHolderProductListNotAvailableBinding.bind(view)

    init {
        binding.tvName.updateLayoutParams<LinearLayout.LayoutParams> {
            height = binding.tvName.lineHeight * 2
        }
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            productsClickListener.onProductClick(item.id)
        }
        binding.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        binding.imgSwap.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onSwapProduct(item)
        }
    }

    override fun attach() {
        super.attach()
        launch {
            likeManager
                .observeLikes()
                .filter{ it.containsKey(item?.id ?: 0) }
                .onEach {
                    val item = item
                    if (item != null) {
                        item.isFavorite = it[item.id] ?: item.isFavorite
                        bindFav(item)
                    }
                }
                .collect()
        }
    }

    override fun bind(item: ProductUI) {
        super.bind(item)

        binding.tvName.text = item.name

        //Price per unit / or order quantity
        if (item.pricePerUnit != 0) {
            binding.tvPricePerUnit.visibility = View.VISIBLE
            binding.tvPricePerUnit.setPricePerUnitText(item.pricePerUnit)
        } else {
            binding.tvPricePerUnit.visibility = View.GONE
        }

        var haveDiscount = false

        //Price
        when(item.priceList.size) {
            1 -> {
                binding.tvPrice.setPriceText(item.priceList.first().currentPrice, itCanBeGift = true)
                binding.tvOldPrice.setPriceText(item.priceList.first().oldPrice)
                binding.tvPriceCondition.visibility = View.GONE
                if (item.priceList.first().currentPrice < item.priceList.first().oldPrice || item.isGift) haveDiscount = true
            }
            else -> {
                val minimalPrice = item.priceList.maxByOrNull { it.requiredAmount }!!
                binding.tvPrice.setMinimalPriceText(minimalPrice.currentPrice)
                binding.tvPriceCondition.setPriceCondition(minimalPrice.requiredAmount)
                binding.tvPriceCondition.visibility = View.VISIBLE
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

        //Favorite
        bindFav(item)

        //If have deposit
        when(item.depositPrice != 0) {
            true -> {
                binding.tvDepositPrice.visibility = View.VISIBLE
                binding.tvDepositPrice.setDepositPriceText(item.depositPrice)
            }
            false -> binding.tvDepositPrice.visibility = View.GONE
        }

        Glide
            .with(itemView.context)
            .load(item.detailPicture)
            .into(binding.imgPicture)
    }

    private fun bindFav(item: ProductUI) {
        when(item.isFavorite) {
            false -> binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_favorite_black))
            true -> binding.imgFavoriteStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_ic_favorite_red))
        }
    }

    private fun getItemByPosition(): ProductUI? {
        return (bindingAdapter as? NotAvailableProductsAdapter)?.getItem(bindingAdapterPosition) as? ProductUI
    }
}