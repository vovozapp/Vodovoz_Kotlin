package com.vodovoz.app.ui.fragment.home.viewholders.homecountries.inneradapter

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.CountryUI

class HomeCountriesDiffUtilCallback: DiffUtil.ItemCallback<CountryUI>() {

    override fun areContentsTheSame(oldItem: CountryUI, newItem: CountryUI): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: CountryUI, newItem: CountryUI): Boolean {
        return oldItem.id == newItem.id
    }
}