package com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter

import android.graphics.Color
import android.graphics.Rect
import android.os.Parcelable
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.ViewHolderSliderPromotionBinding
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter.inneradapterproducts.HomePromotionsProductInnerAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.model.PromotionUI

class HomePromotionsInnerViewHolder(
    view: View,
    clickListener: ProductsClickListener,
    private val promotionsClickListener: PromotionsClickListener,
    cartManager: CartManager,
    likeManager: LikeManager
) : ItemViewHolder<PromotionUI>(view) {

    private val binding: ViewHolderSliderPromotionBinding = ViewHolderSliderPromotionBinding.bind(view)
    private val homePromotionProductAdapter = HomePromotionsProductInnerAdapter(clickListener, cartManager, likeManager)

    private val space = itemView.context.resources.getDimension(R.dimen.space_16).toInt()

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            promotionsClickListener.onPromotionClick(item.id)
        }

        binding.rvProducts.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvProducts.setHasFixedSize(true)

        binding.rvProducts.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        left = space / 2
                        top = space / 2
                        right = space / 2
                    }
                }
            }
        )

        binding.rvProducts.adapter = homePromotionProductAdapter

        binding.advIncludeCard.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            promotionsClickListener.onPromotionAdvClick(item.promotionAdvEntity)
        }
    }

    override fun getState(): Parcelable? {
        return binding.rvProducts.layoutManager?.onSaveInstanceState()
    }

    override fun setState(state: Parcelable) {
        binding.rvProducts.layoutManager?.onRestoreInstanceState(state)
    }

    override fun bind(item: PromotionUI) {
        super.bind(item)
        binding.tvName.text = item.name
        binding.tvTimeLeft.text = item.timeLeft
        binding.tvCustomerCategory.text = item.customerCategory
        binding.cwCustomerCategory.setCardBackgroundColor(Color.parseColor(item.statusColor))

        //binding.advIncludeCard.root.isVisible = item.promotionAdvEntity != null
        //binding.advIncludeCard.advTv.text = item.promotionAdvEntity?.titleHeader

        Glide
            .with(itemView.context)
            .load(item.detailPicture)
            .into(binding.imgImage)

        homePromotionProductAdapter.submitList(item.productUIList)
    }
}