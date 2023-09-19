package com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.ViewHolderSliderProductCategoryBinding
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.HomeProductsInnerAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.decoration.ProductSliderMarginDecoration
import com.vodovoz.app.ui.model.CategoryDetailUI

class HomeCategoriesInnerViewHolder(
    view: View,
    clickListener: ProductsClickListener,
    cartManager: CartManager,
    likeManager: LikeManager
) : ItemViewHolder<CategoryDetailUI>(view) {

    private val binding: ViewHolderSliderProductCategoryBinding =
        ViewHolderSliderProductCategoryBinding.bind(view)

    private val homeProductsAdapter = HomeProductsInnerAdapter(clickListener, cartManager, likeManager)

    private var isAddItemDecoration = false

    init {
        binding.rvProducts.layoutManager = LinearLayoutManager(
            itemView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        binding.rvProducts.adapter = homeProductsAdapter
        binding.rvProducts.setRecycledViewPool(likeManager.fetchViewPool())
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
}