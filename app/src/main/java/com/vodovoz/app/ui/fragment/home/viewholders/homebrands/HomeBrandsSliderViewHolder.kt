package com.vodovoz.app.ui.fragment.home.viewholders.homebrands

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.FragmentSliderBrandBinding
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.model.BrandUI

class HomeBrandsSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: FragmentSliderBrandBinding = FragmentSliderBrandBinding.bind(view)

    init {

    }

    fun bind(items: HomeBrands) {

    }

}