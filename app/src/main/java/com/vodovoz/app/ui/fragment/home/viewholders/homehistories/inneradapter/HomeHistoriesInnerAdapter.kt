package com.vodovoz.app.ui.fragment.home.viewholders.homehistories.inneradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.HistoryUI

class HomeHistoriesInnerAdapter(
    private val clickListener: HomeHistoriesSliderClickListener
) : ListAdapter<HistoryUI, HomeHistoriesInnerViewHolder>(HomeHistoriesInnerDiffUtilCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHistoriesInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_history, parent, false)
        return HomeHistoriesInnerViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: HomeHistoriesInnerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}