package com.vodovoz.app.ui.components.adapter.productPropertiesGroupsAdapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPropertiesGroupBinding
import com.vodovoz.app.ui.model.PropertiesGroupUI
import com.vodovoz.app.util.LogSettings

class PropertiesGroupsAdapter(
    private val space: Int
) : RecyclerView.Adapter<PropertiesGroupViewHolder>() {

    var viewMode = ViewMode.PREVIEW
    var propertiesGroupUIList = listOf<PropertiesGroupUI>()
    set(value) {
        field = value
        Log.i(LogSettings.ID_LOG, "INIT SIZE ${field.size}")
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateViewMode(viewMode: ViewMode) {
        this.viewMode = viewMode
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PropertiesGroupViewHolder(
        binding = ViewHolderPropertiesGroupBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        context = parent.context,
        space = space
    )

    override fun onBindViewHolder(
        holder: PropertiesGroupViewHolder,
        position: Int
    ) = holder.onBind(propertiesGroupUIList[position], viewMode)

    override fun getItemCount() = when(viewMode) {
        ViewMode.PREVIEW -> 1
        ViewMode.ALL -> propertiesGroupUIList.size
    }.apply { Log.i(LogSettings.ID_LOG, "SIZE ${propertiesGroupUIList.size}") }

}