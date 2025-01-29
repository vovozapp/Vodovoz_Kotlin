package com.vodovoz.app.feature.addresses

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.addresses.adapter.AddressesClickListener
import com.vodovoz.app.feature.addresses.adapter.AddressesFlowAdapter
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.util.SwipeToRemoveCallback

class AddressesController(
    private val viewModel: AddressesFlowViewModel,
    listener: AddressesClickListener,
    private val context: Context,
) {
    private val space16 by lazy { context.resources.getDimension(R.dimen.space_16).toInt() }
    private val spaceLastItemBottomSpace by lazy {
        context.resources.getDimension(R.dimen.last_item_bottom_normal_space).toInt()
    }
    private val addressesAdapter = AddressesFlowAdapter(listener)

    fun bind(recyclerView: RecyclerView, refresh: SwipeRefreshLayout) {
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
        }
        bindSwipeToRemove(recyclerView)
    }

    private fun bindRefresh(refresh: SwipeRefreshLayout) {
        refresh.setOnRefreshListener {
            viewModel.refresh()
            refresh.isRefreshing = false
        }
    }

    private fun bindSwipeToRemove(recyclerView: RecyclerView) {
        ItemTouchHelper(object : SwipeToRemoveCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                super.onSwiped(viewHolder, i)
                val item = addressesAdapter.getItem(viewHolder.bindingAdapterPosition) as? AddressUI
                    ?: return
                showDeleteAddressDialog(item.id)
            }
        }).attachToRecyclerView(recyclerView)
    }

    internal fun showDeleteAddressDialog(addressId: Long) {
        MaterialAlertDialogBuilder(context)
            .setMessage("Удалить адрес?")
            .setNegativeButton(context.resources.getString(R.string.cancel)) { dialog, _ ->
                addressesAdapter.notifyDataSetChanged()
                dialog.cancel()
            }
            .setPositiveButton(context.resources.getString(R.string.confirm)) { dialog, _ ->
                viewModel.deleteAddress(addressId)
                dialog.cancel()
            }
            .show()
    }
}