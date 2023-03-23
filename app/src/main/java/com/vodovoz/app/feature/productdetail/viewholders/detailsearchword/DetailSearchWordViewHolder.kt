package com.vodovoz.app.feature.productdetail.viewholders.detailsearchword

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentProductDetailsSearchWordBinding
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.feature.productdetail.viewholders.detailcomments.DetailComments
import com.vodovoz.app.feature.productdetail.viewholders.detailsearchword.inner.SearchWordsFlowAdapter
import com.vodovoz.app.feature.productdetail.viewholders.detailservices.inner.ServicesFlowAdapter
import com.vodovoz.app.ui.decoration.SearchMarginDecoration

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
        binding.rvSearchWords.addItemDecoration(SearchMarginDecoration(space/2))
    }

    override fun bind(item: DetailSearchWord) {
        super.bind(item)

        adapter.submitList(item.searchWordList)
    }
}