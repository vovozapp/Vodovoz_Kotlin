package com.vodovoz.app.common.content

import androidx.annotation.CallSuper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter

open class ItemController(private val itemAdapter: ItemAdapter) {

    fun bind(recyclerView: RecyclerView) {
        initList(recyclerView)
    }

    fun bind(recyclerView: RecyclerView, refresh: SwipeRefreshLayout) {
        initList(recyclerView)
        bindRefresh(refresh)
    }

    fun bind(recyclerView: RecyclerView, list: List<Item>) {
        initList(recyclerView)
        submitList(list)
    }

    fun bind(recyclerView: RecyclerView, fab: ConstraintLayout) {
        initList(recyclerView, fab)
    }

    fun submitList(list: List<Item>) {
        itemAdapter.submitList(list)
    }

    fun submitList(list: List<Item>, i: String) {
        itemAdapter.submitList(list, i)
    }

    @CallSuper
    protected open fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = itemAdapter
        }
    }

    @CallSuper
    protected open fun initList(recyclerView: RecyclerView, fab: ConstraintLayout) {
        with(recyclerView) {
            adapter = itemAdapter
        }
    }

    open fun bindRefresh(refresh: SwipeRefreshLayout) {}
}