package com.vodovoz.app.ui.view_holder

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPropertiesGroupBinding
import com.vodovoz.app.ui.adapter.ProductPropertiesAdapter
import com.vodovoz.app.ui.adapter.ProductPropertyGroupsAdapter
import com.vodovoz.app.ui.decoration.PropertyMarginDecoration
import com.vodovoz.app.ui.model.PropertiesGroupUI

class ProductPropertyGroupViewHolder(
    private val binding: ViewHolderPropertiesGroupBinding,
    private val context: Context,
    private val space: Int
) : RecyclerView.ViewHolder(binding.root) {

    private val productPropertiesAdapter = ProductPropertiesAdapter()

    init {
        binding.propertyRecycler.layoutManager = LinearLayoutManager(context)
        binding.propertyRecycler.adapter = productPropertiesAdapter
        binding.propertyRecycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        binding.propertyRecycler.addItemDecoration(PropertyMarginDecoration(space))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onBind(propertiesGroupUI: PropertiesGroupUI, viewMode: ProductPropertyGroupsAdapter.ViewMode) {
        binding.name.text = propertiesGroupUI.name
        productPropertiesAdapter.viewMode = viewMode
        productPropertiesAdapter.propertyUIList = propertiesGroupUI.propertyUIList
        productPropertiesAdapter.notifyDataSetChanged()
    }

}