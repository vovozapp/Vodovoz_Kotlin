package com.vodovoz.app.ui.components.fragment.productSlider.productAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderProductBinding
import com.vodovoz.app.ui.model.ProductUI

class ProductSliderAdapter(
    private val cardWidth: Int
) : RecyclerView.Adapter<ProductSliderViewHolder>() {

    var sliderProductUIList = listOf<ProductUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ProductSliderViewHolder(
        binding = ViewHolderSliderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        context = parent.context,
        cardWidth = cardWidth
    )

    override fun onBindViewHolder(
        holder: ProductSliderViewHolder,
        position: Int
    ) = holder.onBind(sliderProductUIList[position])

    override fun getItemCount() = sliderProductUIList.size

}