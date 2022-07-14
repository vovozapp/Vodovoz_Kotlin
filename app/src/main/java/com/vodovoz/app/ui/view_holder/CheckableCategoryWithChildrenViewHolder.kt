package com.vodovoz.app.ui.view_holder

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSingleRootCatalogCategoryBinding
import com.vodovoz.app.ui.adapter.SingleRootCatalogAdapter
import com.vodovoz.app.ui.extensions.CatalogTitleExtensions.setNameWithIndent
import com.vodovoz.app.ui.model.CategoryUI
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
        binding.detailController.setOnClickListener { categoryClickSubject.onNext(categoryUI) }
        binding.subcategoryRecycler.layoutManager = LinearLayoutManager(context)
        binding.subcategoryRecycler.adapter = singleRootCatalogAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onBind(categoryUI: CategoryUI, way: List<CategoryUI>) {
        this.categoryUI = categoryUI

        binding.detailController.isChecked = way.last().id == categoryUI.id
        binding.name.setNameWithIndent(categoryUI.name, nestingPosition)

        categoryUI.detailPicture?.let {
            Glide
                .with(context)
                .load(categoryUI.detailPicture)
                .into(binding.image)
        }

        singleRootCatalogAdapter.categoryUIList = categoryUI.categoryUIList
        singleRootCatalogAdapter.way = way
        singleRootCatalogAdapter.notifyDataSetChanged()
    }

}