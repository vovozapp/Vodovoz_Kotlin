package com.vodovoz.app.ui.fragment.home.viewholders.homehistories.inneradapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderHistoryBinding
import com.vodovoz.app.ui.model.HistoryUI

class HomeHistoriesInnerViewHolder(
    view: View,
    private val clickListener: HomeHistoriesSliderClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: ViewHolderSliderHistoryBinding = ViewHolderSliderHistoryBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onHistoryClick(item.id)
        }
    }

    fun bind(history: HistoryUI) {
        Glide
            .with(itemView.context)
            .load(history.detailPicture)
            .into(binding.imgPicture)
    }

    private fun getItemByPosition(): HistoryUI? {
        return (bindingAdapter as? HomeHistoriesInnerAdapter)?.currentList?.get(bindingAdapterPosition)
    }
}