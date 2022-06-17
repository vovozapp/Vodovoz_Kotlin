package com.vodovoz.app.ui.components.fragment.productSlider.categoryDetailAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderProductCategoryBinding
import com.vodovoz.app.ui.model.CategoryDetailUI

class ProductCategorySliderAdapter(
    private val cardWidth: Int
) : RecyclerView.Adapter<ProductCategorySliderViewHolder>() {

    var sliderCategoryUIList = listOf<CategoryDetailUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ProductCategorySliderViewHolder(
        ViewHolderSliderProductCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        context = parent.context,
        cardWidth = cardWidth
    )

    override fun onBindViewHolder(
        holder: ProductCategorySliderViewHolder,
        position: Int
    ) = holder.onBind(sliderCategoryUIList[position], position == sliderCategoryUIList.size - 1)

    override fun getItemCount() = sliderCategoryUIList.size

}