package com.vodovoz.app.feature.productlist.singleroot.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSingleRootCatalogCategoryBinding
import com.vodovoz.app.ui.model.CategoryUI

@SuppressLint("NotifyDataSetChanged")
class SingleRootCatalogAdapter(
    private val nestingPosition: Int,
    private val categoryClickListener: (CategoryUI) -> Unit,
) : RecyclerView.Adapter<CheckableCategoryWithChildrenViewHolder>() {

    var categoryUIList = listOf<CategoryUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = CheckableCategoryWithChildrenViewHolder(
        binding = ViewHolderSingleRootCatalogCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        categoryClickListener = categoryClickListener
    )

    override fun onBindViewHolder(
        holder: CheckableCategoryWithChildrenViewHolder,
        position: Int,
    ) {
        holder.onBind(categoryUIList, position)
    }

    override fun getItemCount() =
       categoryUIList.size
}