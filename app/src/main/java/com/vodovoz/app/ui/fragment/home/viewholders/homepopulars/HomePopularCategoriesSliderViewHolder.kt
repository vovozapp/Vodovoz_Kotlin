package com.vodovoz.app.ui.fragment.home.viewholders.homepopulars

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.FragmentSliderPopularBinding
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.model.CategoryUI

class HomePopularCategoriesSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: FragmentSliderPopularBinding = FragmentSliderPopularBinding.bind(view)

    init {

    }

    fun bind(items: HomePopulars) {

    }


}