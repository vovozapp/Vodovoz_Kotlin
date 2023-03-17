package com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter

import android.graphics.Color
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderSliderPromotionBinding
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter.inneradapterproducts.HomePromotionsProductInnerAdapter
import com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter.inneradapterproducts.HomePromotionsProductInnerClickListener
import com.vodovoz.app.ui.model.PromotionUI

class HomePromotionsInnerViewHolder(
    view: View,
    private val clickListener: HomePromotionsSliderClickListener
) : ItemViewHolder<PromotionUI>(view) {

    private val binding: ViewHolderSliderPromotionBinding = ViewHolderSliderPromotionBinding.bind(view)
    private val homePromotionProductAdapter = HomePromotionsProductInnerAdapter(getHomePromotionsProductInnerClickListener())
    private val space = itemView.context.resources.getDimension(R.dimen.space_16).toInt()

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onPromotionClick(item.id)
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
                        bottom = space / 2
                    }
                }
            }
        )

        binding.rvProducts.adapter = homePromotionProductAdapter
    }

    override fun bind(item: PromotionUI) {
        super.bind(item)
        binding.tvName.text = item.name
        binding.tvTimeLeft.text = item.timeLeft
        binding.tvCustomerCategory.text = item.customerCategory
        binding.cwCustomerCategory.setCardBackgroundColor(Color.parseColor(item.statusColor))

        Glide
            .with(itemView.context)
            .load(item.detailPicture)
            .into(binding.imgImage)

        homePromotionProductAdapter.submitList(item.productUIList)
    }

    private fun getItemByPosition(): PromotionUI? {
        return (bindingAdapter as? HomePromotionsInnerAdapter)?.getItem(bindingAdapterPosition) as? PromotionUI
    }

    private fun getHomePromotionsProductInnerClickListener() : HomePromotionsProductInnerClickListener {
        return object : HomePromotionsProductInnerClickListener {
            override fun onPromotionProductClick(id: Long) {
                clickListener.onPromotionProductClick(id)
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                clickListener.onNotifyWhenBeAvailable(id, name, detailPicture)
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                clickListener.onChangeProductQuantity(id, cartQuantity, oldQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                clickListener.onFavoriteClick(id, isFavorite)
            }
        }
    }
}