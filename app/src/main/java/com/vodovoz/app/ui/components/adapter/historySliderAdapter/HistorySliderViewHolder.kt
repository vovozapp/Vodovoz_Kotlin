package com.vodovoz.app.ui.components.adapter.historySliderAdapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderHistoryBinding
import com.vodovoz.app.ui.model.HistoryUI

class HistorySliderViewHolder(
    private val binding: ViewHolderSliderHistoryBinding,
    private val context: Context,
    private val cardWidth: Int
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.layoutParams = RecyclerView.LayoutParams(
            cardWidth,
            (cardWidth * 1.77).toInt()
        )
    }

    private lateinit var historyUI: HistoryUI

    fun onBind(historyUI: HistoryUI) {
        this.historyUI = historyUI

        Glide
            .with(context)
            .load(historyUI.detailPicture)
            .into(binding.detailPicture)

    }

}