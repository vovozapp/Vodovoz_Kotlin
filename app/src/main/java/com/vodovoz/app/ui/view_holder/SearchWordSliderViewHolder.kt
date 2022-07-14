package com.vodovoz.app.ui.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderSearchWordBinding
import io.reactivex.rxjava3.subjects.PublishSubject

class SearchWordSliderViewHolder(
    private val binding: ViewHolderSliderSearchWordBinding,
    private val onQueryClickSubject: PublishSubject<String>
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.word.setOnClickListener {
            onQueryClickSubject.onNext(query)
        }
    }

    private lateinit var query: String
    fun onBind(query: String) {
        this.query = query
        binding.word.text = query
    }
    
}