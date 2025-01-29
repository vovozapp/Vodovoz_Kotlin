package com.vodovoz.app.feature.productlist.singleroot.adapter

import android.os.Parcelable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderSingleRootCatalogCategoryBinding
import com.vodovoz.app.ui.model.CategoryUI

class SingleRootViewHolder(
    view: View,
    clickListener: SingleRootClickListener,
    nestingPosition: Int
) : ItemViewHolder<CategoryUI>(view) {

    private val binding: ViewHolderSingleRootCatalogCategoryBinding = ViewHolderSingleRootCatalogCategoryBinding.bind(view)

    private val singleRootCatalogAdapter = SingleRootFlowAdapter(
        clickListener = clickListener,
        nestingPosition = nestingPosition + 1
    )


    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onCatClick(item)
        }
        binding.imgDropDown.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onCatClick(item)
            binding.imgDropDown.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_check_round))
        }
    }


    override fun bind(item: CategoryUI) {
        super.bind(item)

    }
}