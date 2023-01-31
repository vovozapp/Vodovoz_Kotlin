package com.vodovoz.app.ui.fragment.home.viewholders.homeproducts

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.FragmentSliderProductBinding
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.model.CategoryDetailUI

class HomeProductsSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: FragmentSliderProductBinding = FragmentSliderProductBinding.bind(view)

    init {

    }

    fun bind(items: HomeProducts) {

    }


}