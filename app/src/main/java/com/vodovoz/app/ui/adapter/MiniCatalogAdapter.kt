package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderCatalogCategoryMiniBinding
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.view_holder.CheckableCategoryViewHolder

class MiniCatalogAdapter : RecyclerView.Adapter<CheckableCategoryViewHolder>() {

    var selectedCategoryId: Long? = null
    var categoryUIList = listOf<CategoryUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = CheckableCategoryViewHolder(
        binding = ViewHolderCatalogCategoryMiniBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) { categoryId ->
        val oldPosition = categoryUIList.indexOfFirst { it.id == selectedCategoryId }
        val newPosition = categoryUIList.indexOfFirst { it.id == categoryId }
        selectedCategoryId = categoryId
        notifyItemChanged(oldPosition)
        notifyItemChanged(newPosition)
    }

    override fun onBindViewHolder(
        holder: CheckableCategoryViewHolder,
        position: Int,
    ) = holder.onBind(
        categoryUI = categoryUIList[position],
        isSelected = categoryUIList[position].id == selectedCategoryId
    )

    override fun getItemCount() = categoryUIList.size

}