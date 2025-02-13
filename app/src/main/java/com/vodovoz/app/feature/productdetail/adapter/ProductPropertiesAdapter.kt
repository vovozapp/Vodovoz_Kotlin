package com.vodovoz.app.feature.productdetail.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPropertyBinding
import com.vodovoz.app.feature.productdetail.adapter.ProductPropertyGroupsAdapter.ViewMode
import com.vodovoz.app.ui.model.PropertyUI

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
    ) {
        if (position in propertyUIList.indices) {
            holder.onBind(propertyUIList[position])
        }
    }

    override fun getItemCount() = when(viewMode) {
        ViewMode.PREVIEW -> if (propertyUIList.size < 4) {
            propertyUIList.size
        } else {
            4
        }
        ViewMode.ALL -> propertyUIList.size
    }

}