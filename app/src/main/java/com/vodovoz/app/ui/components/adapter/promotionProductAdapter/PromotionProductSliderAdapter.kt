package com.vodovoz.app.ui.components.adapter.promotionProductAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderPromotionProductBinding
import com.vodovoz.app.ui.model.ProductUI

class PromotionProductSliderAdapter() : RecyclerView.Adapter<PromotionProductSliderViewHolder>() {

    var productUIList = listOf<ProductUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PromotionProductSliderViewHolder(
        binding = ViewHolderSliderPromotionProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: PromotionProductSliderViewHolder,
        position: Int
    ) = holder.onBind(productUIList[position])

    override fun getItemCount() = productUIList.size

}