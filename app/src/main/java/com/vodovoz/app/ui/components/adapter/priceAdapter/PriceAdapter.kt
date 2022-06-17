package com.vodovoz.app.ui.components.adapter.priceAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPriceBinding
import com.vodovoz.app.ui.model.PriceUI

class PriceAdapter : RecyclerView.Adapter<PriceViewHolder>() {

    var priceUIList = listOf<PriceUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PriceViewHolder(
        binding = ViewHolderPriceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(
        holder: PriceViewHolder,
        position: Int
    ) = holder.onBind(priceUIList[position])

    override fun getItemCount() = priceUIList.size

}