package com.vodovoz.app.feature.home.viewholders.homehistories.inneradapter

import android.view.View
import com.bumptech.glide.Glide
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderSliderHistoryBinding
import com.vodovoz.app.ui.model.HistoryUI

class HomeHistoriesInnerViewHolder(
    view: View,
    private val clickListener: HomeHistoriesSliderClickListener
) : ItemViewHolder<HistoryUI>(view) {

    private val binding: ViewHolderSliderHistoryBinding = ViewHolderSliderHistoryBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onHistoryClick(item.id)
        }
    }

    override fun bind(item: HistoryUI) {
        super.bind(item)
        Glide
            .with(itemView.context)
            .load(item.detailPicture)
            .into(binding.imgPicture)
    }

    private fun getItemByPosition(): HistoryUI? {
        return (bindingAdapter as? HomeHistoriesInnerAdapter)?.getItem(bindingAdapterPosition) as? HistoryUI
    }
}