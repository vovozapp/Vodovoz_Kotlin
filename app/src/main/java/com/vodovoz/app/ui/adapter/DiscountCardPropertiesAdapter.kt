package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderDiscountCardPropertyBinding
import com.vodovoz.app.ui.model.custom.DiscountCardPropertyUI
import com.vodovoz.app.ui.view_holder.DiscountCardPropertyViewHolder

class DiscountCardPropertiesAdapter : RecyclerView.Adapter<DiscountCardPropertyViewHolder>() {

    var discountCardPropertyUIList = listOf<DiscountCardPropertyUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = DiscountCardPropertyViewHolder(
        binding = ViewHolderDiscountCardPropertyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        parent.context
    )

    override fun onBindViewHolder(
        holder: DiscountCardPropertyViewHolder,
        position: Int
    ) = holder.onBind(discountCardPropertyUIList[position])

    override fun getItemCount() = discountCardPropertyUIList.size

}