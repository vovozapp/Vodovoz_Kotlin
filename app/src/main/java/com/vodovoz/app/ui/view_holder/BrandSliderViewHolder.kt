package com.vodovoz.app.ui.view_holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderBrandBinding
import com.vodovoz.app.ui.model.BrandUI
import io.reactivex.rxjava3.subjects.PublishSubject

class BrandSliderViewHolder(
    private val binding: ViewHolderSliderBrandBinding,
    private val onBrandClickSubject: PublishSubject<Long>,
    private val context: Context,
    private val cardWidth: Int
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.layoutParams = RecyclerView.LayoutParams(
            cardWidth,
            (cardWidth * 0.55).toInt()
        )

        binding.root.setOnClickListener {
            onBrandClickSubject.onNext(brandUI.id)
        }
    }

    private lateinit var brandUI: BrandUI

    fun onBind(brandUI: BrandUI) {
        this.brandUI = brandUI

        Glide
            .with(context)
            .load(brandUI.detailPicture)
            .into(binding.detailPicture)

    }

}