package com.vodovoz.app.feature.home.viewholders.homepromotions

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.databinding.FragmentSliderPromotionBinding
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter.HomePromotionsInnerAdapter

class HomePromotionsSliderViewHolder(
    view: View,
    cartManager: CartManager,
    likeManager: LikeManager,
    private val promotionsClickListener: PromotionsClickListener,
    productsClickListener: ProductsClickListener
) : ItemViewHolder<HomePromotions>(view) {

    private val binding: FragmentSliderPromotionBinding = FragmentSliderPromotionBinding.bind(view)
    private val homePromotionsAdapter = HomePromotionsInnerAdapter(productsClickListener, promotionsClickListener, cartManager, likeManager)

    init {
        binding.tvShowAll.setOnClickListener { promotionsClickListener.onShowAllPromotionsClick() }

        binding.vpPromotions.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.vpPromotions.adapter = homePromotionsAdapter
    }

    override fun bind(item: HomePromotions) {
        super.bind(item)

        binding.tvName.text = item.items.title

        homePromotionsAdapter.submitList(item.items.promotionUIList)

        when(item.items.containShowAllButton) {
            true -> binding.tvShowAll.visibility = View.VISIBLE
            false -> binding.tvShowAll.visibility = View.INVISIBLE
        }
    }
}