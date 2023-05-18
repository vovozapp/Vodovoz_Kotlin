package com.vodovoz.app.feature.home.viewholders.hometitle

import android.view.View
import androidx.core.view.isVisible
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderFlowTitleBinding

class HomeTitleViewHolder(
    view: View,
    homeTitleClickListener: HomeTitleClickListener
) : ItemViewHolder<HomeTitle>(view) {

    private val binding: ViewHolderFlowTitleBinding = ViewHolderFlowTitleBinding.bind(view)

    init {
        binding.tvShowAll.setOnClickListener {
            val item = item ?:return@setOnClickListener

            homeTitleClickListener.onShowAllTitleClick(item.type)
        }

    }

    override fun bind(item: HomeTitle) {
        super.bind(item)

        binding.tvName.text = item.name
        binding.tvShowAll.isVisible = item.showAll
        binding.tvShowAll.text = item.showAllName

    }
}