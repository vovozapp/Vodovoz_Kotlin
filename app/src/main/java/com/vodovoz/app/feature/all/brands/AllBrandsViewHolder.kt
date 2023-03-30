package com.vodovoz.app.feature.all.brands

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderBrandWithNameBinding
import com.vodovoz.app.feature.all.AllClickListener
import com.vodovoz.app.ui.model.BrandUI

class AllBrandsViewHolder(
    view: View,
    private val allClickListener: AllClickListener,
) : ItemViewHolder<BrandUI>(view) {

    private val binding: ViewHolderBrandWithNameBinding = ViewHolderBrandWithNameBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            allClickListener.onBrandClick(item.id)
        }
    }

    override fun bind(item: BrandUI) {
        super.bind(item)

        binding.tvName.text = item.name
    }
}