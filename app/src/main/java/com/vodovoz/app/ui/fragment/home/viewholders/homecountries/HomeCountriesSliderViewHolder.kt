package com.vodovoz.app.ui.fragment.home.viewholders.homecountries

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.FragmentSliderCountryBinding
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.model.custom.CountriesSliderBundleUI

class HomeCountriesSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: FragmentSliderCountryBinding = FragmentSliderCountryBinding.bind(view)

    init {

    }

    fun bind(items: HomeCountries) {

    }

}