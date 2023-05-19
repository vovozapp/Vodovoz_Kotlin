package com.vodovoz.app.feature.home.viewholders.homeproductstabs

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderHomeTabsBinding
import com.vodovoz.app.feature.home.viewholders.homeproductstabs.inneradapter.HomeTabsAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration

class HomeProductsTabsViewHolder(
    view: View,
    clickListener: HomeTabsClickListener
) : ItemViewHolder<HomeProductsTabs>(view) {

    private val binding: ViewHolderHomeTabsBinding = ViewHolderHomeTabsBinding.bind(view)
    private val tabsAdapter = HomeTabsAdapter(clickListener)

    init {
        binding.rvTabs.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        val space = itemView.resources.getDimension(R.dimen.space_16).toInt()

        binding.rvTabs.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.left = space
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.right = space
            else rect.right = space/2
            rect.top = space / 2
            rect.bottom = space / 2
        }

        binding.rvTabs.adapter = tabsAdapter
    }

    override fun bind(item: HomeProductsTabs) {
        super.bind(item)
        tabsAdapter.submitList(item.tabsNames)
    }
}