package com.vodovoz.app.ui.components.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderSearchWordBinding

class SearchWordSliderViewHolder(
    private val binding: ViewHolderSliderSearchWordBinding
) : RecyclerView.ViewHolder(binding.root) {
    
    fun onBind(searchWord: String) {
        binding.word.text = searchWord
    }
    
}