package com.vodovoz.app.ui.components.adapter.searchWordAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderSearchWordBinding

class SearchWordAdapter : RecyclerView.Adapter<SearchWordViewHolder>() {
    
    var searchWordList = listOf<String>()
    
    override fun onCreateViewHolder(
        parent: ViewGroup, 
        viewType: Int
    ) = SearchWordViewHolder(
        binding = ViewHolderSliderSearchWordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(
        holder: SearchWordViewHolder,
        position: Int
    ) = holder.onBind(searchWordList[position])

    override fun getItemCount() = searchWordList.size
    
}