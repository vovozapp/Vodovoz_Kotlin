package com.vodovoz.app.ui.view_holder

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderSingleRootCatalogCategoryBinding
import com.vodovoz.app.ui.adapter.SingleRootCatalogAdapter
import com.vodovoz.app.ui.extensions.CatalogTitleExtensions.setNameWithIndent
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.view.Divider
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.subjects.PublishSubject

class CheckableCategoryWithChildrenViewHolder(
    private val binding: ViewHolderSingleRootCatalogCategoryBinding,
    private val context: Context,
    private val categoryClickSubject: PublishSubject<CategoryUI>,
    private val nestingPosition: Int
) : RecyclerView.ViewHolder(binding.root) {

    private val singleRootCatalogAdapter = SingleRootCatalogAdapter(
        categoryClickSubject = categoryClickSubject,
        nestingPosition = nestingPosition + 1
    )

    private lateinit var categoryUI: CategoryUI

    init {
        binding.root.setOnClickListener { categoryClickSubject.onNext(categoryUI) }
        binding.imgDropDown.setOnClickListener {
            categoryClickSubject.onNext(categoryUI)
            binding.imgDropDown.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_check_round))
        }
        binding.rvSubcategories.layoutManager = LinearLayoutManager(context)
        binding.rvSubcategories.adapter = singleRootCatalogAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onBind(categoryUI: CategoryUI, way: List<CategoryUI>) {
        this.categoryUI = categoryUI

        when(way.last().id == categoryUI.id) {
            true -> binding.imgDropDown.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_round_check))
            false -> binding.imgDropDown.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.png_round_uncheck))
        }
        binding.tvName.text = categoryUI.name

        singleRootCatalogAdapter.categoryUIList = categoryUI.categoryUIList
        singleRootCatalogAdapter.way = way
        singleRootCatalogAdapter.notifyDataSetChanged()
    }

}