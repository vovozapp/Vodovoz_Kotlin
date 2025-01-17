package com.vodovoz.app.feature.promotion_filters.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPromotionFilterBinding
import com.vodovoz.app.ui.model.PromotionFilterUI

class PromotionsFiltersAdapter(
    private val selectedFilterId: Long,
    private var promotionFilterUIList: List<PromotionFilterUI>,
    private val onPromotionFilterClickListener: (Long) -> Unit,
) : RecyclerView.Adapter<PromotionFilterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = PromotionFilterViewHolder(
        binding = ViewHolderPromotionFilterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onPromotionFilterClickListener = onPromotionFilterClickListener
    )

    override fun onBindViewHolder(
        holder: PromotionFilterViewHolder,
        position: Int,
    ) = holder.onBind(
        promotionFilterUI = promotionFilterUIList[position],
        isSelected = promotionFilterUIList[position].id == selectedFilterId
    )

    override fun getItemCount() = promotionFilterUIList.size

}