package com.vodovoz.app.ui.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderPopularCategoryBinding
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.subjects.PublishSubject

class PopularCategorySliderViewHolder(
    private val binding: ViewHolderSliderPopularCategoryBinding,
    private val onPopularClickSubject: PublishSubject<Long>
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onPopularClickSubject.onNext(categoryUI.id!!)
        }
    }

    private lateinit var categoryUI: CategoryUI

    fun onBind(categoryUI: CategoryUI) {
        this.categoryUI = categoryUI

        binding.tvName.text = categoryUI.name
    }

}