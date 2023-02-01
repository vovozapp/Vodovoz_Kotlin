package com.vodovoz.app.ui.fragment.home.viewholders.homeorders.inneradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.OrderUI

class HomeOrdersInnerAdapter(
    private val clickListener: HomeOrdersSliderClickListener
) : ListAdapter<OrderUI, HomeOrdersInnerViewHolder>(HomeOrdersDiffUtilCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeOrdersInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_order, parent, false)
        return HomeOrdersInnerViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: HomeOrdersInnerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}