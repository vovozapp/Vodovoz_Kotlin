package com.vodovoz.app.ui.fragment.home.viewholders.homecountries.inneradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.CountryUI

class HomeCountriesInnerAdapter(
    private val clickListener: HomeCountriesSliderClickListener,
    private val cardWidth: Int
) : ListAdapter<CountryUI, HomeCountriesInnerViewHolder>(HomeCountriesDiffUtilCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCountriesInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_country, parent, false)
        return HomeCountriesInnerViewHolder(view, clickListener, cardWidth)
    }

    override fun onBindViewHolder(holder: HomeCountriesInnerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}