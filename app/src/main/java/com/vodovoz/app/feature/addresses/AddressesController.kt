package com.vodovoz.app.feature.addresses

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.addresses.adapter.AddressesClickListener
import com.vodovoz.app.feature.addresses.adapter.AddressesFlowAdapter
import com.vodovoz.app.ui.adapter.AddressesAdapterItemType
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration

class AddressesController(
    private val viewModel: AddressesFlowViewModel,
    listener: AddressesClickListener,
    context: Context
) {
    private val space16 by lazy { context.resources.getDimension(R.dimen.space_16).toInt() }
    private val spaceLastItemBottomSpace by lazy {
        context.resources.getDimension(R.dimen.last_item_bottom_normal_space).toInt()
    }
    private val addressesAdapter = AddressesFlowAdapter(listener)

    fun bind(recyclerView: RecyclerView,  refresh: SwipeRefreshLayout) {
        initList(recyclerView)
        bindRefresh(refresh)
    }

    fun submitList(list: List<Item>) {
        addressesAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = addressesAdapter
            layoutManager = LinearLayoutManager(context)
            addMarginDecoration { rect, view, parent, state ->
                rect.top = space16
                val position = parent.getChildAdapterPosition(view)
                if (parent.adapter?.getItemViewType(position) == AddressesAdapterItemType.ADDRESS.value) {
                    if (position == state.itemCount - 1) rect.bottom = spaceLastItemBottomSpace
                }
            }
        }
    }

    private fun bindRefresh(refresh: SwipeRefreshLayout) {

        refresh.setOnRefreshListener {
            viewModel.refresh()
            refresh.isRefreshing = false
        }
    }
}