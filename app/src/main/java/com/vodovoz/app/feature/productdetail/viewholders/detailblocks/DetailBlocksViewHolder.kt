package com.vodovoz.app.feature.productdetail.viewholders.detailblocks

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentProductDetailsBlocksBinding
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.feature.productdetail.viewholders.detailblocks.inner.BlocksFlowAdapter

class DetailBlocksViewHolder(
    view: View,
    val clickListener: ProductDetailsClickListener,
) : ItemViewHolder<DetailBlocks>(view) {

    private val binding: FragmentProductDetailsBlocksBinding =
        FragmentProductDetailsBlocksBinding.bind(view)
    private val adapter: BlocksFlowAdapter = BlocksFlowAdapter(clickListener)
    internal val space: Int by lazy {
        itemView.context.resources.getDimensionPixelOffset(com.intuit.sdp.R.dimen._4sdp)
    }

    init {
        binding.rvBlocks.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
        binding.rvBlocks.adapter = adapter

        binding.rvBlocks.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State,
                ) {
                    outRect.top = space
                }
            }
        )
    }

    override fun bind(item: DetailBlocks) {
        super.bind(item)

        adapter.submitList(item.items)
    }
}