package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderHistoryBinding
import com.vodovoz.app.ui.view_holder.HistorySliderViewHolder
import com.vodovoz.app.ui.model.HistoryUI
import io.reactivex.rxjava3.subjects.PublishSubject

class HistoriesSliderAdapter(
    private val cardWidth: Int,
    private val onHistoryClickSubject: PublishSubject<Long>
) : RecyclerView.Adapter<HistorySliderViewHolder>() {

    var historyUIList = listOf<HistoryUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HistorySliderViewHolder(
        binding = ViewHolderSliderHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onHistoryClickSubject = onHistoryClickSubject,
        context = parent.context,
        cardWidth = cardWidth
    )

    override fun onBindViewHolder(
        holder: HistorySliderViewHolder,
        position: Int
    ) = holder.onBind(historyUIList[position])

    override fun getItemCount() = historyUIList.size

}