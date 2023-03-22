package com.vodovoz.app.feature.productdetail.viewholders.detailtabs

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentProductDetailsTabsBinding

class DetailTabsViewHolder(
    view: View,
    val fragment: Fragment
) : ItemViewHolder<DetailTabs>(view) {

    private val binding: FragmentProductDetailsTabsBinding = FragmentProductDetailsTabsBinding.bind(view)

    init {
        initViewPager()
    }

    override fun bind(item: DetailTabs) {
        super.bind(item)
        binding.tvConsumerInfo.text = item.productDetailUI.consumerInfo
    }

    private fun initViewPager() {
        val item = item ?: return
        val adapter = DetailTabsViewPagerAdapter(fragment, item.productDetailUI)
        binding.viewPager.adapter = adapter
        binding.aboutTabs.setupWithViewPager(binding.viewPager, listOf("ОПИСАНИЕ", "ХАРАКТЕРИСТИКИ"))
    }

    private fun TabLayout.setupWithViewPager(viewPager: ViewPager2, labels: List<String>) {

        if (labels.size != viewPager.adapter?.itemCount)
            throw Exception("The size of list and the tab count should be equal!")

        TabLayoutMediator(this, viewPager) { tab, position ->
            tab.text = labels[position]
        }.attach()
    }
}