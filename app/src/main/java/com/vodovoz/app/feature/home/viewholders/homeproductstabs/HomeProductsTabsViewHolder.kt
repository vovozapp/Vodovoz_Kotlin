package com.vodovoz.app.feature.home.viewholders.homeproductstabs

import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        binding.rvTabs.addMarginDecoration { rect, viewTab, parent, state ->
            if (parent.getChildAdapterPosition(viewTab) == 0) rect.left = space
            if (parent.getChildAdapterPosition(viewTab) == state.itemCount - 1) rect.right = space
            else rect.right = space/2
            rect.top = space / 2
            rect.bottom = space / 2
        }

        binding.rvTabs.adapter = tabsAdapter

        binding.rvTabs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onScrollInnerRecycler(this@HomeProductsTabsViewHolder)
            }
        })
    }

    override fun getState(): Parcelable? {
        return binding.rvTabs.layoutManager?.onSaveInstanceState()
    }

    override fun setState(state: Parcelable) {
        binding.rvTabs.layoutManager?.onRestoreInstanceState(state)
    }

    override fun bind(item: HomeProductsTabs) {
        super.bind(item)
        tabsAdapter.submitList(item.tabsNames)
    }
}