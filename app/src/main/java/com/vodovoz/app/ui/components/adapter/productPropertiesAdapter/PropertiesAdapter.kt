package com.vodovoz.app.ui.components.adapter.productPropertiesAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPropertyBinding
import com.vodovoz.app.ui.components.adapter.productPropertiesGroupsAdapter.ViewMode
import com.vodovoz.app.ui.model.PropertyUI

class PropertiesAdapter : RecyclerView.Adapter<PropertyViewHolder>() {

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
    ) = PropertyViewHolder(
        binding = ViewHolderPropertyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(
        holder: PropertyViewHolder,
        position: Int
    ) = holder.onBind(propertyUIList[position])

    override fun getItemCount() = when(viewMode) {
        ViewMode.PREVIEW -> 4
        ViewMode.ALL -> propertyUIList.size
    }

}