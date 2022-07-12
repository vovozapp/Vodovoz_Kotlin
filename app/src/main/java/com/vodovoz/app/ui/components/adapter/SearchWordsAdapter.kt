package com.vodovoz.app.ui.components.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderSearchWordBinding
import com.vodovoz.app.ui.components.view_holder.SearchWordSliderViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class SearchWordsAdapter(
    private val onQueryClickSubject: PublishSubject<String>
) : RecyclerView.Adapter<SearchWordSliderViewHolder>() {
    
    var searchWordList = listOf<String>()
    
    override fun onCreateViewHolder(
        parent: ViewGroup, 
        viewType: Int
    ) = SearchWordSliderViewHolder(
        binding = ViewHolderSliderSearchWordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onQueryClickSubject = onQueryClickSubject
    )

    override fun onBindViewHolder(
        holder: SearchWordSliderViewHolder,
        position: Int
    ) = holder.onBind(searchWordList[position])

    override fun getItemCount() = searchWordList.size
    
}