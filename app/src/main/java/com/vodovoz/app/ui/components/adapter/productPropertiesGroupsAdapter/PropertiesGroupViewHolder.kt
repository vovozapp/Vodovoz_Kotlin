package com.vodovoz.app.ui.components.adapter.productPropertiesGroupsAdapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPropertiesGroupBinding
import com.vodovoz.app.ui.components.adapter.productPropertiesAdapter.PropertiesAdapter
import com.vodovoz.app.ui.components.adapter.productPropertiesAdapter.PropertyMarginDecoration
import com.vodovoz.app.ui.model.PropertiesGroupUI

class PropertiesGroupViewHolder(
    private val binding: ViewHolderPropertiesGroupBinding,
    private val context: Context,
    private val space: Int
) : RecyclerView.ViewHolder(binding.root) {

    private val propertiesAdapter = PropertiesAdapter()

    init {
        binding.propertyRecycler.layoutManager = LinearLayoutManager(context)
        binding.propertyRecycler.adapter = propertiesAdapter
        binding.propertyRecycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        binding.propertyRecycler.addItemDecoration(PropertyMarginDecoration(space))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onBind(propertiesGroupUI: PropertiesGroupUI, viewMode: ViewMode) {
        binding.name.text = propertiesGroupUI.name
        propertiesAdapter.viewMode = viewMode
        propertiesAdapter.propertyUIList = propertiesGroupUI.propertyUIList
        propertiesAdapter.notifyDataSetChanged()
    }

}