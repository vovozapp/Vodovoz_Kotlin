package com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.inneradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.PromotionUI

class HomePromotionsInnerAdapter(
    private val clickListener: HomePromotionsSliderClickListener
) : ListAdapter<PromotionUI, HomePromotionsInnerViewHolder>(HomePromotionsDiffUtilCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePromotionsInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_promotion, parent, false)
        return HomePromotionsInnerViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: HomePromotionsInnerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}