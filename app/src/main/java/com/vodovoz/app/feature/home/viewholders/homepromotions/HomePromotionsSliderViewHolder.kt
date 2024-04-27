package com.vodovoz.app.feature.home.viewholders.homepromotions

import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentSliderPromotionBinding
import com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter.HomePromotionsInnerAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener

class HomePromotionsSliderViewHolder(
    view: View,
    cartManager: CartManager,
    likeManager: LikeManager,
   promotionsClickListener: PromotionsClickListener,
    productsClickListener: ProductsClickListener
) : ItemViewHolder<HomePromotions>(view) {

    private val binding: FragmentSliderPromotionBinding = FragmentSliderPromotionBinding.bind(view)
    private val homePromotionsAdapter = HomePromotionsInnerAdapter(productsClickListener, promotionsClickListener, cartManager, likeManager)

    init {
        binding.vpPromotions.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpPromotions.adapter = homePromotionsAdapter
    }

    override fun bind(item: HomePromotions) {
        super.bind(item)

        val color = if(item.whiteBg) {
            R.color.white
        } else {
            R.color.light_bg
        }
        binding.vpPromotions.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    color
                )
            )
        homePromotionsAdapter.submitList(item.items.promotionUIList)
    }
}