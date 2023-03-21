package com.vodovoz.app.feature.productdetail.viewholders.detailprices

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentProductDetailsPricesBinding
import com.vodovoz.app.feature.productdetail.viewholders.detailprices.inner.PricesFlowAdapter

class DetailPricesViewHolder(
    view: View
) : ItemViewHolder<DetailPrices>(view) {

    private val binding: FragmentProductDetailsPricesBinding = FragmentProductDetailsPricesBinding.bind(view)
    private val adapter: PricesFlowAdapter = PricesFlowAdapter()

    init {
        binding.rvPrices.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        binding.rvPrices.adapter = adapter

    }

    override fun bind(item: DetailPrices) {
        super.bind(item)

        adapter.submitList(item.priceUiList)
    }


}