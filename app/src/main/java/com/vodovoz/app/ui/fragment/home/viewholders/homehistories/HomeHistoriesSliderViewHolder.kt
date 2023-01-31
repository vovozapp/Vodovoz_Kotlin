package com.vodovoz.app.ui.fragment.home.viewholders.homehistories

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.FragmentSliderBannerBinding
import com.vodovoz.app.databinding.FragmentSliderHistoryBinding
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.model.BannerUI
import com.vodovoz.app.ui.model.HistoryUI

class HomeHistoriesSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: FragmentSliderHistoryBinding = FragmentSliderHistoryBinding.bind(view)

    init {

    }

    fun bind(items: HomeHistories) {

    }

}