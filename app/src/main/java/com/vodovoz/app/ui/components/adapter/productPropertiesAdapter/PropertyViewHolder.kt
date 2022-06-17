package com.vodovoz.app.ui.components.adapter.productPropertiesAdapter

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPropertyBinding
import com.vodovoz.app.ui.model.PropertyUI

class PropertyViewHolder(
    private val binding: ViewHolderPropertyBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(propertyUI: PropertyUI) {
        binding.name.text = propertyUI.name
        binding.value.text = propertyUI.value
    }

}