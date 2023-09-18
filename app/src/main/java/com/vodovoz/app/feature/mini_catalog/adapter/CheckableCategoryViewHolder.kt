package com.vodovoz.app.feature.mini_catalog.adapter

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderCatalogCategoryMiniBinding
import com.vodovoz.app.ui.model.CategoryUI

class CheckableCategoryViewHolder(
    private val binding: ViewHolderCatalogCategoryMiniBinding,
    private val onCategoryClickListener: (Long?) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onCategoryClickListener(categoryUI.id)
        }
        binding.imgDropDown.setOnClickListener {
            binding.imgDropDown.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.png_round_check
                )
            )
            onCategoryClickListener(categoryUI.id)
        }
    }

    private lateinit var categoryUI: CategoryUI

    fun onBind(categoryUI: CategoryUI, isSelected: Boolean) {
        this.categoryUI = categoryUI
        binding.tvName.text = categoryUI.name

        when (isSelected) {
            true -> binding.imgDropDown.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.png_round_check
                )
            )
            false -> binding.imgDropDown.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.png_round_uncheck
                )
            )
        }
    }

}