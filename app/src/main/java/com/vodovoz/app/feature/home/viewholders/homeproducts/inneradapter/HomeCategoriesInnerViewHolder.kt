package com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderSliderProductCategoryBinding
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.HomeProductsInnerAdapter
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.HomeProductsInnerClickListener
import com.vodovoz.app.ui.decoration.ProductSliderMarginDecoration
import com.vodovoz.app.ui.model.CategoryDetailUI

class HomeCategoriesInnerViewHolder(
    view: View,
    private val clickListener: HomeCategoriesInnerClickListener
) : ItemViewHolder<CategoryDetailUI>(view) {

    private val binding: ViewHolderSliderProductCategoryBinding =
        ViewHolderSliderProductCategoryBinding.bind(view)

    private val homeProductsAdapter = HomeProductsInnerAdapter(getHomeProductsInnerClickListener())

    private var isAddItemDecoration = false

    init {
        binding.rvProducts.layoutManager = LinearLayoutManager(
            itemView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        binding.rvProducts.adapter = homeProductsAdapter
    }

    override fun bind(item: CategoryDetailUI) {
        super.bind(item)

        homeProductsAdapter.submitList(item.productUIList)

        if (!isAddItemDecoration) {
            binding.rvProducts.addItemDecoration(
                ProductSliderMarginDecoration(
                    space = itemView.context.resources.getDimension(R.dimen.space_16).toInt(),
                    itemCount = item.productUIList.size,
                    isLast = bindingAdapterPosition == (bindingAdapter as? HomeCategoriesInnerAdapter)?.itemCount?.minus(1)
                )
            )
            isAddItemDecoration = true
        }
    }

    private fun getHomeProductsInnerClickListener() : HomeProductsInnerClickListener {
        return object : HomeProductsInnerClickListener {
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