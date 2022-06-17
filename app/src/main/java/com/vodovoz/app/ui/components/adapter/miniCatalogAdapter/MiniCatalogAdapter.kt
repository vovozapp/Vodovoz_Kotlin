package com.vodovoz.app.ui.components.adapter.miniCatalogAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderCatalogCategoryMiniBinding
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.subjects.PublishSubject

@SuppressLint("NotifyDataSetChanged")
class MiniCatalogAdapter(
    private val categoryClickSubject: PublishSubject<CategoryUI>,
    private val nestingPosition: Int
) : RecyclerView.Adapter<MiniCatalogViewHolder>() {

    var way = listOf<CategoryUI>()
    var categoryUIList = listOf<CategoryUI>()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = MiniCatalogViewHolder(
        binding = ViewHolderCatalogCategoryMiniBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        context = parent.context,
        nestingPosition = nestingPosition,
        categoryClickSubject = categoryClickSubject
    )

    override fun onBindViewHolder(
        holder: MiniCatalogViewHolder,
        position: Int
    ) {
        if (nestingPosition <= way.indices.last) {
            holder.onBind(way[nestingPosition], way)
        } else {
            holder.onBind(categoryUIList[position], way)
        }
    }

    override fun getItemCount() =
        if (nestingPosition <= way.indices.last) 1
        else if (nestingPosition == way.indices.last + 1) categoryUIList.size
        else 0

}