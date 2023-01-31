package com.vodovoz.app.ui.fragment.home.viewholders.homeorders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.FragmentSliderOrderBinding
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener

class HomeOrdersSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: FragmentSliderOrderBinding = FragmentSliderOrderBinding.bind(view)

    init {

    }

    fun bind(items: HomeOrders) {

    }

}