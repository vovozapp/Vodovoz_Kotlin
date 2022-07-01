package com.vodovoz.app.ui.components.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderCatalogCategoryBinding
import com.vodovoz.app.ui.components.view_holder.DropdownCategoryViewHolder
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.subjects.PublishSubject

@SuppressLint("NotifyDataSetChanged")
class MainCatalogAdapter(
    private val categoryClickSubject: PublishSubject<CategoryUI>,
    private val nestingPosition: Int
) : RecyclerView.Adapter<DropdownCategoryViewHolder>() {

    var categoryUIList = listOf<CategoryUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = DropdownCategoryViewHolder(
        binding = ViewHolderCatalogCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        context = parent.context,
        categoryClickSubject = categoryClickSubject,
        nestingPosition = nestingPosition
    )

    override fun onBindViewHolder(
        holder: DropdownCategoryViewHolder,
        position: Int
    ) = holder.onBind(categoryUIList[position])

    override fun getItemCount() = categoryUIList.size

}