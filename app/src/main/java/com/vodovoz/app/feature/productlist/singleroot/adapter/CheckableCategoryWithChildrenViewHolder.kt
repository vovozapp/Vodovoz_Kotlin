package com.vodovoz.app.feature.productlist.singleroot.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderSingleRootCatalogCategoryBinding
import com.vodovoz.app.ui.model.CategoryUI

class CheckableCategoryWithChildrenViewHolder(
    private val binding: ViewHolderSingleRootCatalogCategoryBinding,
    private val categoryClickListener: (CategoryUI) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var categoryUI: List<CategoryUI>

    init {
        binding.root.setOnClickListener {
            categoryClickListener(categoryUI[position])
        }
        binding.imgDropDown.setOnClickListener {
            categoryClickListener(categoryUI[position])
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onBind(categoryUI: List<CategoryUI>, position: Int) {
        this.categoryUI = categoryUI
        binding.tvName.text = categoryUI[position].name
        binding.imgDropDown
    }
}