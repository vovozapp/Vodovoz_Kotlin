package com.vodovoz.app.ui.fragment.home.viewholders.homebrands.inneradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.BrandUI

class HomeBrandsInnerAdapter(
    private val clickListener: HomeBrandsSliderClickListener
) : ListAdapter<BrandUI, HomeBrandsInnerViewHolder>(HomeBrandsInnerDiffUtilCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBrandsInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_brand, parent, false)
        return HomeBrandsInnerViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: HomeBrandsInnerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}