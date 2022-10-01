package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderBrandFilterValueBinding
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.view_holder.CategoryTabViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class CategoryTabsAdapter(
    private val onCategoryClickSubject: PublishSubject<Long>
) : RecyclerView.Adapter<CategoryTabViewHolder>() {

    var selectedId: Long? = null
    var categoryUIList = listOf<CategoryUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = CategoryTabViewHolder(
        binding = ViewHolderBrandFilterValueBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onCategoryClickSubject = onCategoryClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: CategoryTabViewHolder,
        position: Int
    ) = holder.onBind(
        categoryUI = categoryUIList[position],
        isSelected = categoryUIList[position].id == selectedId
    )

    override fun getItemCount() = categoryUIList.size

}