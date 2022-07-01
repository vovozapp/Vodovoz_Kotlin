package com.vodovoz.app.ui.components.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderCatalogCategoryMiniBinding
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.subjects.PublishSubject

class CheckableCategoryViewHolder(
    private val binding: ViewHolderCatalogCategoryMiniBinding,
    private val onCategoryClickSubject: PublishSubject<Long>
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onCategoryClickSubject.onNext(categoryUI.id!!)
        }
        binding.detailController.setOnClickListener {
            onCategoryClickSubject.onNext(categoryUI.id!!)
        }
    }

    private lateinit var categoryUI: CategoryUI

    fun onBind(categoryUI: CategoryUI, isSelected: Boolean) {
        this.categoryUI = categoryUI
        binding.name.text = categoryUI.name

        binding.detailController.isChecked = isSelected
    }

}