package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPromotionFilterBinding
import com.vodovoz.app.ui.model.PromotionFilterUI
import com.vodovoz.app.ui.view_holder.PromotionFilterViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionsFiltersAdapter(
    private val selectedFilterId: Long,
    private var promotionFilterUIList: List<PromotionFilterUI>,
    private val onPromotionFilterClickSubject: PublishSubject<Long>
) : RecyclerView.Adapter<PromotionFilterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PromotionFilterViewHolder(
        binding = ViewHolderPromotionFilterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onPromotionFilterClickSubject = onPromotionFilterClickSubject
    )

    override fun onBindViewHolder(
        holder: PromotionFilterViewHolder,
        position: Int
    ) = holder.onBind(
        promotionFilterUI = promotionFilterUIList[position],
        isSelected = promotionFilterUIList[position].id == selectedFilterId
    )

    override fun getItemCount() = promotionFilterUIList.size

}