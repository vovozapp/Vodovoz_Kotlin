package com.vodovoz.app.ui.components.adapter.searchWordAdapter

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderSearchWordBinding

class SearchWordViewHolder(
    private val binding: ViewHolderSliderSearchWordBinding
) : RecyclerView.ViewHolder(binding.root) {
    
    fun onBind(searchWord: String) {
        binding.word.text = searchWord
    }
    
}