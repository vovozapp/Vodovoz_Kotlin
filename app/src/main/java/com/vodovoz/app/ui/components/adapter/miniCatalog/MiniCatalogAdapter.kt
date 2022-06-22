package com.vodovoz.app.ui.components.adapter.miniCatalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderCatalogCategoryMiniBinding
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class MiniCatalogAdapter(
    private val onCategoryClickSubject: PublishSubject<Long>
) : RecyclerView.Adapter<MiniCatalogViewHolder>() {

    init {
        onCategoryClickSubject.subscribeBy { categoryId ->
            val oldPosition = categoryUIList.indexOfFirst { it.id == selectedCategoryId }
            val newPosition = categoryUIList.indexOfFirst { it.id == categoryId }
            selectedCategoryId = categoryId
            notifyItemChanged(oldPosition)
            notifyItemChanged(newPosition)
        }
    }

    var selectedCategoryId: Long? = null
    var categoryUIList = listOf<CategoryUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = MiniCatalogViewHolder(
        binding = ViewHolderCatalogCategoryMiniBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onCategoryClickSubject = onCategoryClickSubject
    )

    override fun onBindViewHolder(
        holder: MiniCatalogViewHolder,
        position: Int
    ) = holder.onBind(
        categoryUI = categoryUIList[position],
        isSelected = categoryUIList[position].id == selectedCategoryId
    )

    override fun getItemCount() = categoryUIList.size

}