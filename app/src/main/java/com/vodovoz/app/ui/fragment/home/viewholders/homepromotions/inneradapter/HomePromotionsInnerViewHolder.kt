package com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.inneradapter

import android.graphics.Color
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderSliderPromotionBinding
import com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.inneradapter.inneradapterproducts.HomePromotionsProductInnerAdapter
import com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.inneradapter.inneradapterproducts.HomePromotionsProductInnerClickListener
import com.vodovoz.app.ui.model.PromotionUI

class HomePromotionsInnerViewHolder(
    view: View,
    private val clickListener: HomePromotionsSliderClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: ViewHolderSliderPromotionBinding =
        ViewHolderSliderPromotionBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onPromotionClick(item.id)
        }
    }

    fun bind(promotion: PromotionUI) {
        binding.tvName.text = promotion.name
        binding.tvTimeLeft.text = promotion.timeLeft
        binding.tvCustomerCategory.text = promotion.customerCategory
        binding.cwCustomerCategory.setCardBackgroundColor(Color.parseColor(promotion.statusColor))

        Glide
            .with(itemView.context)
            .load(promotion.detailPicture)
            .into(binding.imgImage)

        initRv(promotion)
    }

    private fun initRv(promotion: PromotionUI) {
        binding.rvProducts.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvProducts.setHasFixedSize(true)
        val space = itemView.context.resources.getDimension(R.dimen.space_16).toInt()
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
        binding.rvProducts.adapter = HomePromotionsProductInnerAdapter(getHomePromotionsProductInnerClickListener()).apply {
            submitList(promotion.productUIList)
        }
    }

    private fun getItemByPosition(): PromotionUI? {
        return (bindingAdapter as? HomePromotionsInnerAdapter)?.currentList?.get(
            bindingAdapterPosition
        )
    }

    private fun getHomePromotionsProductInnerClickListener() : HomePromotionsProductInnerClickListener {
        return object : HomePromotionsProductInnerClickListener {
            override fun onPromotionProductClick(id: Long) {
                clickListener.onPromotionProductClick(id)
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                clickListener.onNotifyWhenBeAvailable(id, name, detailPicture)
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int) {
                clickListener.onChangeProductQuantity(id, cartQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                clickListener.onFavoriteClick(id, isFavorite)
            }
        }
    }
}