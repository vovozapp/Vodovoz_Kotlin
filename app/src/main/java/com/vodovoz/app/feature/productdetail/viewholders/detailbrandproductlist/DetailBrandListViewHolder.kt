package com.vodovoz.app.feature.productdetail.viewholders.detailbrandproductlist

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentProductDetailsBrandProductListBinding
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner.AvailableProductsAdapter
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener

class DetailBrandListViewHolder(
    view: View,
    val clickListener: ProductDetailsClickListener,
    private val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager,
    private val cartManager: CartManager
) : ItemViewHolder<DetailBrandList>(view) {


    private val binding: FragmentProductDetailsBrandProductListBinding = FragmentProductDetailsBrandProductListBinding.bind(view)

    private val space: Int by lazy { itemView.context.resources.getDimension(R.dimen.space_16).toInt() }

    private val productsAdapter = AvailableProductsAdapter(productsClickListener, likeManager, cartManager)

    init {
        binding.nextPage.setOnClickListener { clickListener.onNextPageClick() }

        binding.brandProductRecycler.layoutManager = LinearLayoutManager(itemView.context)
        binding.brandProductRecycler.adapter = productsAdapter
        binding.brandProductRecycler.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        top = space
                        bottom = space
                        right = space
                    }
                }
            }
        )
    }

    override fun bind(item: DetailBrandList) {
        super.bind(item)

        productsAdapter.submitList(item.productUiList)
    }
}