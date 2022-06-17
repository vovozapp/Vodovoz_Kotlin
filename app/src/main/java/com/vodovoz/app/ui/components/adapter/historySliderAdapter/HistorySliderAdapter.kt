package com.vodovoz.app.ui.components.adapter.historySliderAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderHistoryBinding
import com.vodovoz.app.ui.model.HistoryUI

class HistorySliderAdapter(
    private val cardWidth: Int
) : RecyclerView.Adapter<HistorySliderViewHolder>() {

    var historyUIList = listOf<HistoryUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HistorySliderViewHolder(
        binding = ViewHolderSliderHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        context = parent.context,
        cardWidth = cardWidth
    )

    override fun onBindViewHolder(
        holder: HistorySliderViewHolder,
        position: Int
    ) = holder.onBind(historyUIList[position])

    override fun getItemCount() = historyUIList.size

}