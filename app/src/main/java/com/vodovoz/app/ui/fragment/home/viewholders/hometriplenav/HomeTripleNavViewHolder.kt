package com.vodovoz.app.ui.fragment.home.viewholders.hometriplenav

import android.view.View
import com.vodovoz.app.databinding.FragmentTripleNavigationHomeBinding
import com.vodovoz.app.ui.base.content.itemadapter.Item
import com.vodovoz.app.ui.base.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener

class HomeTripleNavViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : ItemViewHolder<Item>(view) {

    private val binding: FragmentTripleNavigationHomeBinding = FragmentTripleNavigationHomeBinding.bind(view)

    init {
        binding.lastPurchases.setOnClickListener { clickListener.onLastPurchasesClick() }
        binding.favorite.setOnClickListener { clickListener.onShowAllFavoritesClick() }
        binding.ordersHistory.setOnClickListener { clickListener.onOrdersHistoryClick() }
    }

    fun bind() {

    }
}