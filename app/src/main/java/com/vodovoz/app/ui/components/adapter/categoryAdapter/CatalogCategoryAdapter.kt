package com.vodovoz.app.ui.components.adapter.categoryAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderCatalogCategoryBinding
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.subjects.PublishSubject

@SuppressLint("NotifyDataSetChanged")
class CatalogCategoryAdapter(
    private val categoryClickSubject: PublishSubject<CategoryUI>,
    private val nestingPosition: Int
) : RecyclerView.Adapter<CatalogCategoryViewHolder>() {

    var categoryUIList = listOf<CategoryUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = CatalogCategoryViewHolder(
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
        holder: CatalogCategoryViewHolder,
        position: Int
    ) = holder.onBind(categoryUIList[position])

    override fun getItemCount() = categoryUIList.size

}