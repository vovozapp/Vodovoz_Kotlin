package com.vodovoz.app.feature.productdetail.viewholders.detailtabs

import android.view.View
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentProductDetailsTabsBinding
import com.vodovoz.app.ui.adapter.ProductPropertyGroupsAdapter

class DetailTabsViewHolder(
    view: View
) : ItemViewHolder<DetailTabs>(view) {

    private val binding: FragmentProductDetailsTabsBinding = FragmentProductDetailsTabsBinding.bind(view)
    private var isExpanded = false
    private val space: Int by lazy { itemView.context.resources.getDimension(R.dimen.space_16).toInt() }
    private val productPropertyGroupsAdapter: ProductPropertyGroupsAdapter by lazy { ProductPropertyGroupsAdapter(space) }

    override fun bind(item: DetailTabs) {
        super.bind(item)
        binding.tvConsumerInfo.text = item.productDetailUI.consumerInfo

        binding.tvAboutProduct.originalText = HtmlCompat.fromHtml(item.productDetailUI.detailText, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        if (isExpanded) {
            binding.tvAboutProduct.toggle()
        }

        if (productPropertyGroupsAdapter.viewMode == ProductPropertyGroupsAdapter.ViewMode.ALL) {
            binding.allProperties.visibility = View.GONE
        }
        binding.allProperties.setOnClickListener {
            productPropertyGroupsAdapter.updateViewMode(ProductPropertyGroupsAdapter.ViewMode.ALL)
            binding.allProperties.visibility = View.GONE
        }

        binding.propertiesGroupRecycler.layoutManager = LinearLayoutManager(itemView.context)
        binding.propertiesGroupRecycler.adapter = productPropertyGroupsAdapter.also {
            it.propertiesGroupUIList = item.productDetailUI.propertiesGroupUIList
        }

        binding.aboutTabs.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab?.position) {
                        0 -> {
                            binding.tvAboutProduct.isVisible = true
                            binding.properties.isVisible = false
                        }
                        1 -> {
                            binding.tvAboutProduct.isVisible = false
                            binding.properties.isVisible = true
                        }
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            }
        )
    }

}