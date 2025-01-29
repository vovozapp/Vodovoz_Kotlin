package com.vodovoz.app.feature.home.viewholders.hometitle

import android.view.View
import androidx.core.view.isVisible
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderFlowTitleBinding
import com.vodovoz.app.util.extensions.color

class HomeTitleViewHolder(
    view: View,
    homeTitleClickListener: HomeTitleClickListener? = null
) : ItemViewHolder<HomeTitle>(view) {

    private val binding: ViewHolderFlowTitleBinding = ViewHolderFlowTitleBinding.bind(view)

    init {
        binding.tvShowAll.setOnClickListener {
            val item = item ?:return@setOnClickListener
            homeTitleClickListener?.onShowAllTitleClick(item)
        }

    }

    override fun bind(item: HomeTitle) {
        super.bind(item)

        //if (item.lightBg) {
        //    binding.root.setBackgroundColor(itemView.context.color(R.color.white))
        //} else {
        //    binding.root.setBackgroundColor(itemView.context.color(R.color.light_bg))
        //}

        binding.dividerTop.isVisible = item.showTopDivider

        if (item.categoryProductsName.isNotEmpty()) {
            binding.tvName.text = item.categoryProductsName
        } else {
            binding.tvName.text = item.name
        }

        binding.tvShowAll.isVisible = item.showAll
        binding.tvShowAll.text = item.showAllName

    }
}