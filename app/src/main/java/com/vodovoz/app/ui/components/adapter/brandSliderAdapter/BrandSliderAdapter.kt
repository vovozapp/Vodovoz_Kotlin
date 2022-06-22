package com.vodovoz.app.ui.components.adapter.brandSliderAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderBrandBinding
import com.vodovoz.app.ui.model.BrandUI
import io.reactivex.rxjava3.subjects.PublishSubject

class BrandSliderAdapter(
    private val onBrandClickSubject: PublishSubject<Long>,
    private val cardWidth: Int
) : RecyclerView.Adapter<BrandSliderViewHolder>() {

    var brandUIList = listOf<BrandUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BrandSliderViewHolder(
        binding = ViewHolderSliderBrandBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onBrandClickSubject = onBrandClickSubject,
        context = parent.context,
        cardWidth = cardWidth
    )

    override fun onBindViewHolder(
        holder: BrandSliderViewHolder,
        position: Int
    ) = holder.onBind(brandUIList[position])

    override fun getItemCount() = brandUIList.size

}