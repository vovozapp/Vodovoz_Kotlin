package com.vodovoz.app.ui.fragment.home.viewholders.homepopulars.inneradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.CategoryUI

class HomePopularsInnerAdapter(
    private val clickListener: HomePopularCategoriesSliderClickListener
) : ListAdapter<CategoryUI, HomePopularsInnerViewHolder>(HomePopularsDiffUtilCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePopularsInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_popular_category, parent, false)
        return HomePopularsInnerViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: HomePopularsInnerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}