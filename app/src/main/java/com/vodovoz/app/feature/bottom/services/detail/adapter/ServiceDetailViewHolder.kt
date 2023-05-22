package com.vodovoz.app.feature.bottom.services.detail.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.ItemServiceDetailBinding
import com.vodovoz.app.feature.bottom.services.detail.model.ServiceDetailBlockUI
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener

class ServiceDetailViewHolder(
    view: View,
    cartManager: CartManager,
    likeManager: LikeManager,
    productsClickListener: ProductsClickListener
) : ItemViewHolder<ServiceDetailBlockUI>(view) {

    private val binding: ItemServiceDetailBinding = ItemServiceDetailBinding.bind(view)

    private val serviceDetailProductsAdapter = ServiceDetailAdapter(cartManager, likeManager, productsClickListener)

    private val space = itemView.context.resources.getDimension(R.dimen.space_16).toInt()

    init {
        binding.rvProductsBlock.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
        binding.rvProductsBlock.setHasFixedSize(true)

        binding.rvProductsBlock.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        left = space / 2
                        top = space / 2
                        right = space / 2
                        bottom = space / 2
                    }
                }
            }
        )

        binding.rvProductsBlock.adapter = serviceDetailProductsAdapter
    }

    override fun bind(item: ServiceDetailBlockUI) {
        super.bind(item)

        binding.txtBlockTitle.text = item.blockTitle

        serviceDetailProductsAdapter.submitList(item.productList)

    }
}