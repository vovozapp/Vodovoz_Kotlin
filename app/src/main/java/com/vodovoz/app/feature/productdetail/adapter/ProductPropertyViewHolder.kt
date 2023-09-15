package com.vodovoz.app.feature.productdetail.adapter

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPropertyBinding
import com.vodovoz.app.ui.model.PropertyUI

class ProductPropertyViewHolder(
    private val binding: ViewHolderPropertyBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(propertyUI: PropertyUI) {
        binding.tvName.text = propertyUI.name
        binding.tvValue.text = propertyUI.value
    }

}