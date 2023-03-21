package com.vodovoz.app.feature.home.viewholders.homeproducts

import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadhamwi.tabsync.TabbedListMediator
import com.google.android.material.tabs.TabLayout
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.databinding.FragmentSliderProductBinding
import com.vodovoz.app.databinding.ViewCustomTabBinding
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.BOTTOM_PROD
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.DISCOUNT
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.NOVELTIES
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.TOP_PROD
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.HomeCategoriesInnerAdapter
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener

class HomeProductsSliderViewHolder(
    view: View,
    private val productsShowAllListener: ProductsShowAllListener,
    productsClickListener: ProductsClickListener,
    cartManager: CartManager,
    likeManager: LikeManager
) : ItemViewHolder<HomeProducts>(view) {

    private val binding: FragmentSliderProductBinding = FragmentSliderProductBinding.bind(view)
    private val space: Int by lazy { itemView.resources.getDimension(R.dimen.space_16).toInt() }
    private val homeCategoriesAdapter = HomeCategoriesInnerAdapter(productsClickListener, cartManager, likeManager)

    init {
        binding.rvCategories.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        binding.rvCategories.adapter = homeCategoriesAdapter

        binding.tlCategories.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.view?.findViewById<TextView>(R.id.name)?.setTextColor(
                        ContextCompat.getColor(itemView.context,
                            R.color.bluePrimary)
                    )
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.view?.findViewById<TextView>(R.id.name)?.setTextColor(
                        ContextCompat.getColor(itemView.context,
                            R.color.blackTextLight)
                    )
                }
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            }
        )
    }

    override fun bind(item: HomeProducts) {
        super.bind(item)

        homeCategoriesAdapter.submitList(item.items)

        initShowAllProductsButtons(item)
        updateCategoryTabs(item)
    }

    private fun initShowAllProductsButtons(items: HomeProducts) {
        binding.tvShowAll.setOnClickListener {
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

    private fun updateCategoryTabs(items: HomeProducts) {
        when(items.items.size) {
            1 -> {
                binding.tlCategories.visibility = View.GONE
                binding.llSingleTitleContainer.visibility = View.VISIBLE
                binding.tvName.text = items.items.first().name
                when (items.productsSliderConfig.containShowAllButton) {
                    true -> binding.tvShowAll.visibility = View.VISIBLE
                    false -> binding.tvShowAll.visibility = View.INVISIBLE
                }
            }
            else -> {
                binding.tlCategories.visibility = View.VISIBLE
                binding.llSingleTitleContainer.visibility = View.GONE
                for (index in items.items.indices) {
                    binding.tlCategories.newTab().apply {
                        val customTabBinding = ViewCustomTabBinding.inflate(
                            LayoutInflater.from(itemView.context), null, false
                        )

                        val marginEnd: Int
                        val marginStart: Int
                        val marginTop = space/4
                        val marginBottom = space/4

                        when (index) {
                            0 -> {
                                marginStart = space
                                marginEnd = space/2
                            }
                            items.items.indices.last -> {
                                marginEnd = space
                                marginStart = space/2
                            }
                            else -> {
                                marginEnd = space/2
                                marginStart = space/2
                            }
                        }

                        customTabBinding.card.layoutParams = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            topMargin = marginTop
                            bottomMargin = marginBottom
                            leftMargin = marginStart
                            rightMargin = marginEnd
                        }

                        customTabBinding.name.text = items.items[index].name
                        customView = customTabBinding.root
                        binding.tlCategories.addTab(this)
                    }
                }
                val tab = TabbedListMediator(
                    binding.rvCategories,
                    binding.tlCategories,
                    listOf()
                )
                tab.updateMediatorWithNewIndices(items.items.indices.toList())
                if (!tab.isAttached()) tab.attach()
            }
        }
    }

}