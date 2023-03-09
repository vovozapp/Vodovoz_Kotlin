package com.vodovoz.app.ui.fragment.home.viewholders.homeproducts

import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadhamwi.tabsync.TabbedListMediator
import com.google.android.material.tabs.TabLayout
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderProductBinding
import com.vodovoz.app.databinding.ViewCustomTabBinding
import com.vodovoz.app.ui.base.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.HomeProducts.Companion.BOTTOM_PROD
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.HomeProducts.Companion.DISCOUNT
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.HomeProducts.Companion.NOVELTIES
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.HomeProducts.Companion.TOP_PROD
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.inneradapter.HomeCategoriesInnerAdapter
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.inneradapter.HomeCategoriesInnerClickListener

class HomeProductsSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : ItemViewHolder<HomeProducts>(view) {

    private val binding: FragmentSliderProductBinding = FragmentSliderProductBinding.bind(view)
    private val space: Int by lazy { itemView.resources.getDimension(R.dimen.space_16).toInt() }
    private val homeCategoriesAdapter = HomeCategoriesInnerAdapter(getHomeCategoriesInnerClickListener())

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

        /*when(items.productsSliderConfig.largeTitle) {
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
        }*/

        initShowAllProductsButtons(item)
        updateCategoryTabs(item)
    }


    private fun getHomeCategoriesInnerClickListener() : HomeCategoriesInnerClickListener {
        return object : HomeCategoriesInnerClickListener {
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

    private fun initShowAllProductsButtons(items: HomeProducts) {
        binding.tvShowAll.setOnClickListener {
            items.items.first().id?.let { categoryId ->
                when(items.productsType) {
                    DISCOUNT -> clickListener.showAllDiscountProducts(categoryId)
                    TOP_PROD -> clickListener.showAllTopProducts(categoryId)
                    NOVELTIES -> clickListener.showAllNoveltiesProducts(categoryId)
                    BOTTOM_PROD -> clickListener.showAllBottomProducts(categoryId)
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