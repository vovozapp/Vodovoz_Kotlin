package com.vodovoz.app.ui.fragment.home.viewholders.homecountries.inneradapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderCountryBinding
import com.vodovoz.app.ui.model.CountryUI

class HomeCountriesInnerViewHolder(
    view: View,
    private val clickListener: HomeCountriesSliderClickListener,
    private val cardWidth: Double
) : RecyclerView.ViewHolder(view) {

    private val binding: ViewHolderSliderCountryBinding = ViewHolderSliderCountryBinding.bind(view)

    init {
        binding.root.layoutParams = RecyclerView.LayoutParams(
            (cardWidth * 0.92).toInt(),
            (cardWidth * (2.5/3.0)).toInt()
        )
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onCountryClick(item.id)
        }
    }

    fun bind(country: CountryUI) {
        binding.tvName.text = country.name

        Glide
            .with(itemView.context)
            .load(country.detailPicture)
            .into(binding.imgFlag)
    }

    private fun getItemByPosition(): CountryUI? {
        return (bindingAdapter as? HomeCountriesInnerAdapter)?.currentList?.get(bindingAdapterPosition)
    }
}