package com.vodovoz.app.ui.fragment.home.viewholders.homebanners.inneradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.BannerUI

class HomeBannersInnerAdapter(
    private val clickListener: HomeBannersSliderClickListener
) : ListAdapter<BannerUI, HomeBannersInnerViewHolder>(HomeBannersInnerDiffUtilCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBannersInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_banner, parent, false)
        return HomeBannersInnerViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: HomeBannersInnerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}