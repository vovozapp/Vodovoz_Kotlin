package com.vodovoz.app.feature.productdetail.viewholders.detailslisttitles

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderFlowDetailsTitleBinding
import com.vodovoz.app.util.extensions.debugLog

class DetailsTitleViewHolder(
    view: View,
) : ItemViewHolder<DetailsTitle>(view) {

    private val binding: ViewHolderFlowDetailsTitleBinding =
        ViewHolderFlowDetailsTitleBinding.bind(view)

    override fun bind(item: DetailsTitle) {
        super.bind(item)

        debugLog { item.categoryProductsName }
        if (item.categoryProductsName.isNotEmpty()) {
            binding.tvName.text = item.categoryProductsName
        } else {
            binding.tvName.text = item.name
        }
    }
}