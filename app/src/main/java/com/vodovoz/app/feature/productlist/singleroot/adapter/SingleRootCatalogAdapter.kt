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

    var way = listOf<CategoryUI>()
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
        context = parent.context,
        nestingPosition = nestingPosition,
        categoryClickListener = categoryClickListener
    )

    override fun onBindViewHolder(
        holder: CheckableCategoryWithChildrenViewHolder,
        position: Int,
    ) {
        if (nestingPosition <= way.indices.last) {
            holder.onBind(way[nestingPosition], way)
        } else {
            holder.onBind(categoryUIList[position], way)
        }
    }

    override fun getItemCount() =
        if (nestingPosition <= way.indices.last) 1
        else if (nestingPosition == way.indices.last + 1) categoryUIList.size
        else 0

}