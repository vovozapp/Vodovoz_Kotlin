package com.vodovoz.app.feature.home.viewholders.homesections.tabs

import android.view.View
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderHomeSectionsTabsBinding
import com.vodovoz.app.ui.model.ParentSectionDataUI
import com.vodovoz.app.util.extensions.color

class HomeSectionsTabsViewHolder (
    view: View,
    private val clickListener: (String) -> Unit,
) : ItemViewHolder<ParentSectionDataUI>(view){

    private val binding = ViewHolderHomeSectionsTabsBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener(item.title)
        }
    }

    override fun bind(item: ParentSectionDataUI) {
        super.bind(item)

        if (item.isSelected) {
            binding.tvName.setTextColor(itemView.context.color(R.color.bluePrimary))
        } else {
            binding.tvName.setTextColor(itemView.context.color(R.color.text_black))
        }
        binding.tvName.text = item.title
    }
}