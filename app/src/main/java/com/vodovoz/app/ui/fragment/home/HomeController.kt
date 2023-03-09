package com.vodovoz.app.ui.fragment.home

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainAdapter
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.base.content.itemadapter.Item

class HomeController(
    private val context: Context,
    lifecycleOwner: LifecycleOwner,
    private val viewModel: HomeFlowViewModel,
    listener: HomeMainClickListener
) {
    private val homeMainAdapter = HomeMainAdapter(listener)

    fun bind(recyclerView: RecyclerView, refresh: SwipeRefreshLayout) {
        initList(recyclerView)
        bindRefresh(refresh)
    }

    fun submitList(list: List<Item>) {
        homeMainAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = homeMainAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun bindRefresh(refresh: SwipeRefreshLayout) {

        refresh.setOnRefreshListener {
            viewModel.refresh()
            refresh.isRefreshing = false
        }
    }
}