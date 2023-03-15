package com.vodovoz.app.feature.home.viewholders.homecountries.inneradapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderCountryBinding
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.CountryUI

class HomeCountriesInnerViewHolder(
    view: View,
    private val clickListener: HomeCountriesSliderClickListener,
    private val cardWidth: Double
) : ItemViewHolder<CountryUI>(view) {

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

    override fun bind(item: CountryUI) {
        super.bind(item)
        binding.tvName.text = item.name

        Glide
            .with(itemView.context)
            .load(item.detailPicture)
            .into(binding.imgFlag)
    }

    private fun getItemByPosition(): CountryUI? {
        return (bindingAdapter as? HomeCountriesInnerAdapter)?.getItem(bindingAdapterPosition) as? CountryUI
    }
}