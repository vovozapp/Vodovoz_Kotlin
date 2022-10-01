package com.vodovoz.app.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPropertyBinding
import com.vodovoz.app.ui.adapter.ProductPropertyGroupsAdapter.ViewMode
import com.vodovoz.app.ui.model.PropertyUI
import com.vodovoz.app.ui.view_holder.ProductPropertyViewHolder

class ProductPropertiesAdapter : RecyclerView.Adapter<ProductPropertyViewHolder>() {

    var propertyUIList = listOf<PropertyUI>()
    var viewMode = ViewMode.PREVIEW

    @SuppressLint("NotifyDataSetChanged")
    fun updateViewMode(viewMode: ViewMode) {
        this.viewMode = viewMode
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ProductPropertyViewHolder(
        binding = ViewHolderPropertyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(
        holder: ProductPropertyViewHolder,
        position: Int
    ) = holder.onBind(propertyUIList[position])

    override fun getItemCount() = when(viewMode) {
        ViewMode.PREVIEW -> 4
        ViewMode.ALL -> propertyUIList.size
    }

}