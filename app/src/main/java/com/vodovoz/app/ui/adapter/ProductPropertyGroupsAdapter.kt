package com.vodovoz.app.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPropertiesGroupBinding
import com.vodovoz.app.ui.model.PropertiesGroupUI
import com.vodovoz.app.ui.view_holder.ProductPropertyGroupViewHolder

class ProductPropertyGroupsAdapter(
    private val space: Int
) : RecyclerView.Adapter<ProductPropertyGroupViewHolder>() {

    var viewMode = ViewMode.PREVIEW
    var propertiesGroupUIList = listOf<PropertiesGroupUI>()
    set(value) {
        field = value
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateViewMode(viewMode: ViewMode) {
        this.viewMode = viewMode
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ProductPropertyGroupViewHolder(
        binding = ViewHolderPropertiesGroupBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        context = parent.context,
        space = space
    )

    override fun onBindViewHolder(
        holder: ProductPropertyGroupViewHolder,
        position: Int
    ) = holder.onBind(propertiesGroupUIList[position], viewMode)

    override fun getItemCount() = when(viewMode) {
        ViewMode.PREVIEW -> 1
        ViewMode.ALL -> propertiesGroupUIList.size
    }

    enum class ViewMode {
        PREVIEW, ALL
    }
}