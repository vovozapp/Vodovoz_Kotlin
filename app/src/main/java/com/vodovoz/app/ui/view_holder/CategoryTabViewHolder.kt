package com.vodovoz.app.ui.view_holder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderBrandFilterValueBinding
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.subjects.PublishSubject

class CategoryTabViewHolder(
    private val binding: ViewHolderBrandFilterValueBinding,
    private val onCategoryClickSubject: PublishSubject<Long>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { onCategoryClickSubject.onNext(categoryUI.id!!) }
    }

    private lateinit var categoryUI: CategoryUI

    fun onBind(categoryUI: CategoryUI, isSelected: Boolean) {
        this.categoryUI = categoryUI

        binding.name.text = categoryUI.name

        when(isSelected) {
            true -> binding.name.setTextColor(ContextCompat.getColor(context, R.color.bluePrimary))
            false -> binding.name.setTextColor(ContextCompat.getColor(context, R.color.blackTextPrimary))
        }
    }

}