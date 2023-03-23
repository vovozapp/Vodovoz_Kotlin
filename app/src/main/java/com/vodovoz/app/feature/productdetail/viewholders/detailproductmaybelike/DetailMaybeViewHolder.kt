package com.vodovoz.app.feature.productdetail.viewholders.detailproductmaybelike

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentProductDetailsBrandProductListBinding
import com.vodovoz.app.databinding.FragmentProductDetailsMaybeLikeProductListBinding
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner.AvailableProductsAdapter
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.feature.productdetail.viewholders.detailbrandproductlist.DetailBrandList
import com.vodovoz.app.feature.productdetail.viewholders.detailcomments.DetailComments

class DetailMaybeViewHolder(
    view: View,
    val clickListener: ProductDetailsClickListener,
    private val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager,
    private val cartManager: CartManager
) : ItemViewHolder<DetailMaybeLike>(view) {


    private val binding: FragmentProductDetailsMaybeLikeProductListBinding = FragmentProductDetailsMaybeLikeProductListBinding.bind(view)

    private val space: Int by lazy { itemView.context.resources.getDimension(R.dimen.space_16).toInt() }

    private val productsAdapter = AvailableProductsAdapter(productsClickListener, likeManager, cartManager)

    init {
        binding.tvNextPage.setOnClickListener { clickListener.onNextPageMaybeLikeClick() }

        binding.rvProducts.layoutManager = GridLayoutManager(itemView.context, 2)
        binding.rvProducts.adapter = productsAdapter
        binding.rvProducts.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        if (parent.getChildAdapterPosition(view) % 2 == 0) {
                            left = space
                            right = space/2
                        } else {
                            left = space/2
                            right = space
                        }
                        top = space
                        bottom = space
                    }
                }
            }
        )
    }

    override fun bind(item: DetailMaybeLike) {
        super.bind(item)

        productsAdapter.submitList(item.productUiList)

        if (item.pageAmount == 1) {
            binding.tvNextPage.visibility = View.GONE
        } else {
            binding.tvNextPage.visibility = View.VISIBLE
        }
    }

}