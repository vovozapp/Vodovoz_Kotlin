package com.vodovoz.app.ui.view_holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderCountryBinding
import com.vodovoz.app.ui.model.CountryUI
import io.reactivex.rxjava3.subjects.PublishSubject

class CountrySliderViewHolder(
    private val binding: ViewHolderSliderCountryBinding,
    private val onCountryClickSubject: PublishSubject<Long>,
    private val context: Context,
    private val cardWidth: Int
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.layoutParams = RecyclerView.LayoutParams(
            cardWidth,
            (cardWidth * (2.5/3.0)).toInt()
        )
        binding.root.setOnClickListener { onCountryClickSubject.onNext(countryUI.id) }
    }

    private lateinit var countryUI: CountryUI

    fun onBind(countryUI: CountryUI) {
        this.countryUI = countryUI

        binding.tvName.text = countryUI.name

        Glide
            .with(context)
            .load(countryUI.detailPicture)
            .into(binding.imgFlag)

    }

}