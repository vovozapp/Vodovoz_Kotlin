package com.vodovoz.app.feature.productdetail.viewholders.detailprices

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentProductDetailsPricesBinding
import com.vodovoz.app.feature.productdetail.viewholders.detailprices.inner.PricesFlowAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration

class DetailPricesViewHolder(
    view: View
) : ItemViewHolder<DetailPrices>(view) {

    private val binding: FragmentProductDetailsPricesBinding = FragmentProductDetailsPricesBinding.bind(view)
    private val adapter: PricesFlowAdapter = PricesFlowAdapter()
    private val space = itemView.resources.getDimension(R.dimen.space_16).toInt()

    init {
        binding.rvPrices.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        binding.rvPrices.adapter = adapter

        binding.rvPrices.addMarginDecoration {  rect, vie, parent, _ ->
            if (parent.getChildAdapterPosition(vie) == 0) {
                rect.left = space/1
            }
            rect.right = space/2
            rect.bottom = space/4
            rect.top = space/4
        }
    }

    override fun bind(item: DetailPrices) {
        super.bind(item)

        adapter.submitList(item.priceUiList)
    }


}