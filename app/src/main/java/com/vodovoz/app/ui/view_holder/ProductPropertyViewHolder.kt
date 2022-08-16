package com.vodovoz.app.ui.view_holder

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