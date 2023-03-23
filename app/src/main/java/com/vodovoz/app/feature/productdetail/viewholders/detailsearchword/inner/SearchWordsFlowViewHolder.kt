package com.vodovoz.app.feature.productdetail.viewholders.detailsearchword.inner

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderSliderSearchWordBinding
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener

class SearchWordsFlowViewHolder(
    view: View,
    private val clickListener: ProductDetailsClickListener
) : ItemViewHolder<SearchWordItem>(view) {

    private val binding: ViewHolderSliderSearchWordBinding = ViewHolderSliderSearchWordBinding.bind(view)

    init {
        binding.tvWord.setOnClickListener {
            val item = item ?:return@setOnClickListener
            clickListener.onQueryClick(item.query)
        }
    }

    override fun bind(item: SearchWordItem) {
        super.bind(item)
        binding.tvWord.text = item.query
    }

}