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
    private val context: Context,
    private val categoryClickListener: (CategoryUI) -> Unit,
    private val nestingPosition: Int,
) : RecyclerView.ViewHolder(binding.root) {

    private val singleRootCatalogAdapter = SingleRootCatalogAdapter(
        categoryClickListener = categoryClickListener,
        nestingPosition = nestingPosition + 1
    )

    private lateinit var categoryUI: CategoryUI

    init {
        binding.root.setOnClickListener {
            categoryClickListener(categoryUI)
        }
        binding.imgDropDown.setOnClickListener {
            categoryClickListener(categoryUI)
            binding.imgDropDown.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.ic_check_round
                )
            )
        }
        binding.rvSubcategories.layoutManager = LinearLayoutManager(context)
        binding.rvSubcategories.adapter = singleRootCatalogAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onBind(categoryUI: CategoryUI, way: List<CategoryUI>) {
        this.categoryUI = categoryUI

        when (way.last().id == categoryUI.id) {
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
        binding.tvName.text = categoryUI.name

        singleRootCatalogAdapter.categoryUIList = categoryUI.categoryUIList
        singleRootCatalogAdapter.way = way
        singleRootCatalogAdapter.notifyDataSetChanged()
    }

}