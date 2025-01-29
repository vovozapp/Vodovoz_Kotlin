package com.vodovoz.app.feature.catalog.adapter

import android.os.Parcelable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderCatalogCategoryBinding
import com.vodovoz.app.ui.extensions.CatalogTitleExtensions.setNameWithIndent
import com.vodovoz.app.ui.model.CategoryUI

class CatalogFlowViewHolder(
    view: View,
    clickListener: CatalogFlowClickListener,
    private val nestingPosition: Int
) : ItemViewHolder<CategoryUI>(view) {

    private val binding: ViewHolderCatalogCategoryBinding = ViewHolderCatalogCategoryBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onCategoryClick(item)
        }
    }

    override fun bind(item: CategoryUI) {
        super.bind(item)

        binding.name.text = item.name

        item.detailPicture?.let {
            Glide
                .with(itemView.context)
                .load(item.detailPicture)
                .into(binding.image)
        }

    }
}