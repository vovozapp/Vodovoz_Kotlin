package com.vodovoz.app.feature.home.viewholders.homeproducts

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadhamwi.tabsync.TabbedListMediator
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentSliderProductBinding
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.BOTTOM_PROD
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.DISCOUNT
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.NOVELTIES
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.TOP_PROD
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.HomeCategoriesInnerAdapter
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.HomeProductsInnerAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.decoration.ProductSliderMarginDecoration

class HomeProductsSliderViewHolder(
    view: View,
    private val productsShowAllListener: ProductsShowAllListener,
    productsClickListener: ProductsClickListener,
    cartManager: CartManager,
    likeManager: LikeManager
) : ItemViewHolder<HomeProducts>(view) {

    private val binding: FragmentSliderProductBinding = FragmentSliderProductBinding.bind(view)

    private val homeProductsAdapter = HomeProductsInnerAdapter(productsClickListener, cartManager, likeManager)

    private var isAddItemDecoration = false

    init {
        binding.rvCategories.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        binding.rvCategories.adapter = homeProductsAdapter
        binding.rvCategories.setRecycledViewPool(likeManager.fetchViewPool())

        binding.tvShowAll.setOnClickListener {
            val items = item ?: return@setOnClickListener
            items.items.first().id?.let { categoryId ->
                when(items.productsType) {
                    DISCOUNT -> productsShowAllListener.showAllDiscountProducts(categoryId)
                    TOP_PROD -> productsShowAllListener.showAllTopProducts(categoryId)
                    NOVELTIES -> productsShowAllListener.showAllNoveltiesProducts(categoryId)
                    BOTTOM_PROD -> productsShowAllListener.showAllBottomProducts(categoryId)
                    else -> {}
                }
            }
        }
    }

    override fun bind(item: HomeProducts) {
        super.bind(item)

        homeProductsAdapter.submitList(item.prodList)

        if (!isAddItemDecoration) {
            binding.rvCategories.addItemDecoration(
                ProductSliderMarginDecoration(
                    space = itemView.context.resources.getDimension(R.dimen.space_16).toInt(),
                    itemCount = item.items.first().productUIList.size,
                    isLast = bindingAdapterPosition == (bindingAdapter as? HomeCategoriesInnerAdapter)?.itemCount?.minus(1)
                )
            )
            isAddItemDecoration = true
        }
        updateCategoryTabs(item)
    }

    private fun updateCategoryTabs(items: HomeProducts) {
        when(items.items.size) {
            1 -> {
                binding.tlCategories.visibility = View.GONE
                binding.tvName.visibility = View.VISIBLE
                binding.tvShowAll.visibility = View.VISIBLE
                binding.tvName.text = items.items.first().name
                when (items.productsSliderConfig.containShowAllButton) {
                    true -> binding.tvShowAll.visibility = View.VISIBLE
                    false -> binding.tvShowAll.visibility = View.INVISIBLE
                }
            }
            else -> {
                binding.tlCategories.visibility = View.VISIBLE
                binding.tvName.visibility = View.GONE
                binding.tvShowAll.visibility = View.GONE

                binding.tlCategories.removeAllTabs()
                items.items.forEach {
                    binding.tlCategories.addTab(binding.tlCategories.newTab().setText(it.name))
                }

                TabbedListMediator(
                    binding.rvCategories,
                    binding.tlCategories,
                    items.items.indices.toList()
                ).attach()
            }
        }
    }

}