package com.vodovoz.app.feature.home.viewholders.homepromotions

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.databinding.FragmentSliderPromotionBinding
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter.HomePromotionsInnerAdapter
import com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter.HomePromotionsSliderClickListener

class HomePromotionsSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener,
    private val cartManager: CartManager
) : ItemViewHolder<HomePromotions>(view) {

    private val binding: FragmentSliderPromotionBinding = FragmentSliderPromotionBinding.bind(view)
    private val homePromotionsAdapter = HomePromotionsInnerAdapter(getHomePromotionsSliderClickListener(), cartManager)

    init {
        binding.tvShowAll.setOnClickListener { clickListener.onShowAllPromotionsClick() }

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

        /*if (items.items.promotionUIList.isNotEmpty()) {
            when (items.items.promotionUIList.first().productUIList.isEmpty()) {
                true -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.tvName.setTextAppearance(R.style.TextViewHeaderBlackBold)
                } else {
                    binding.tvName.setTextAppearance(null, R.style.TextViewHeaderBlackBold)
                }
                false -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.tvName.setTextAppearance(R.style.TextViewMediumBlackBold)
                } else {
                    binding.tvName.setTextAppearance(null, R.style.TextViewMediumBlackBold)
                }
            }
        }*/
    }

    private fun getHomePromotionsSliderClickListener() : HomePromotionsSliderClickListener {
        return object : HomePromotionsSliderClickListener {
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

            override fun onPromotionClick(id: Long) {
                clickListener.onPromotionClick(id)
            }
        }
    }

}