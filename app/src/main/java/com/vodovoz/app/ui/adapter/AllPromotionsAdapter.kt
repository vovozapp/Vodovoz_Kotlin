package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPromotionBinding
import com.vodovoz.app.ui.view_holder.PromotionWithoutProductsViewHolder
import com.vodovoz.app.ui.model.PromotionUI
import io.reactivex.rxjava3.subjects.PublishSubject

class AllPromotionsAdapter(
    private val onPromotionClickSubject: PublishSubject<Long>,
) : RecyclerView.Adapter<PromotionWithoutProductsViewHolder>() {

    var promotionUIList = listOf<PromotionUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PromotionWithoutProductsViewHolder(
        binding = ViewHolderPromotionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onPromotionClickSubject = onPromotionClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: PromotionWithoutProductsViewHolder,
        position: Int
    ) = holder.onBind(promotionUIList[position])

    override fun getItemCount() = promotionUIList.size


}