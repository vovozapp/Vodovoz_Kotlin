package com.vodovoz.app.feature.productdetail.viewholders.detailservices

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentProductDetailsPricesBinding
import com.vodovoz.app.databinding.FragmentProductDetailsServicesBinding
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.feature.productdetail.viewholders.detailcomments.DetailComments
import com.vodovoz.app.feature.productdetail.viewholders.detailprices.DetailPrices
import com.vodovoz.app.feature.productdetail.viewholders.detailprices.inner.PricesFlowAdapter
import com.vodovoz.app.feature.productdetail.viewholders.detailservices.inner.ServicesFlowAdapter
import com.vodovoz.app.feature.productdetail.viewholders.detailservices.inner.ServicesFlowViewHolder

class DetailServicesViewHolder(
    view: View,
    val clickListener: ProductDetailsClickListener
) : ItemViewHolder<DetailServices>(view) {

    private val binding: FragmentProductDetailsServicesBinding = FragmentProductDetailsServicesBinding.bind(view)
    private val adapter: ServicesFlowAdapter = ServicesFlowAdapter()
    private val space: Int by lazy { itemView.context.resources.getDimension(R.dimen.space_16).toInt() }

    init {
        binding.rvServices.layoutManager = GridLayoutManager(itemView.context, 2)

        binding.rvServices.adapter = adapter

        binding.rvServices.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State,
                ) {
                    with(outRect) {
                        if (parent.getChildAdapterPosition(view) % 2 == 0) {
                            left = space
                            right = space/4
                        } else {
                            left = space/4
                            right = space
                        }
                        top = space
                        bottom = space
                    }
                }
            }
        )
    }

    override fun bind(item: DetailServices) {
        super.bind(item)

        adapter.submitList(item.items)
    }
}