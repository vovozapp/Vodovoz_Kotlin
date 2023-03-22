package com.vodovoz.app.feature.productdetail.viewholders.detailtabs

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vodovoz.app.ui.fragment.product_info.ProductInfoFragment
import com.vodovoz.app.ui.fragment.product_properties.ProductPropertiesFragment
import com.vodovoz.app.ui.model.ProductDetailUI

class DetailTabsViewPagerAdapter(fragment: Fragment, private val productDetailUI: ProductDetailUI) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> ProductInfoFragment.newInstance(productDetailUI.detailText)
            1 -> ProductPropertiesFragment.newInstance(productDetailUI.propertiesGroupUIList)
            else -> ProductInfoFragment.newInstance(productDetailUI.detailText)
        }
    }

}