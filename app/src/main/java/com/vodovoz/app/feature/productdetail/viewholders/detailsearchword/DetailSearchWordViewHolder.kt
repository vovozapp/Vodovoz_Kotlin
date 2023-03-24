package com.vodovoz.app.feature.productdetail.viewholders.detailsearchword

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentProductDetailsSearchWordBinding
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.feature.productdetail.viewholders.detailsearchword.inner.SearchWordsFlowAdapter

class DetailSearchWordViewHolder(
    view: View,
    val clickListener: ProductDetailsClickListener
) : ItemViewHolder<DetailSearchWord>(view) {

    private val binding: FragmentProductDetailsSearchWordBinding = FragmentProductDetailsSearchWordBinding.bind(view)
    private val adapter: SearchWordsFlowAdapter = SearchWordsFlowAdapter(clickListener)
    private val space: Int by lazy { itemView.context.resources.getDimension(R.dimen.space_16).toInt() }

    init {
        binding.rvSearchWords.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvSearchWords.adapter = adapter
        binding.rvSearchWords.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                with(outRect) {
                    top = space/2
                    bottom = space
                    left = space/2
                }
            }
        })
    }

    override fun bind(item: DetailSearchWord) {
        super.bind(item)

        adapter.submitList(item.searchWordList)
    }
}