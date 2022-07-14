package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderCountryBinding
import com.vodovoz.app.ui.view_holder.CountrySliderViewHolder
import com.vodovoz.app.ui.model.CountryUI
import io.reactivex.rxjava3.subjects.PublishSubject

class CountriesSliderAdapter(
    private val onCountryClickSubject: PublishSubject<Long>,
    private val cardWidth: Int
) : RecyclerView.Adapter<CountrySliderViewHolder>() {

    var countryUIList = listOf<CountryUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CountrySliderViewHolder(
        binding = ViewHolderSliderCountryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onCountryClickSubject = onCountryClickSubject,
        context = parent.context,
        cardWidth = cardWidth
    )

    override fun onBindViewHolder(
        holder: CountrySliderViewHolder,
        position: Int
    ) = holder.onBind(countryUIList[position])

    override fun getItemCount() = countryUIList.size

}