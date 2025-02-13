package com.vodovoz.app.feature.productdetail.viewholders.detailbrandproductlist

import android.graphics.Rect
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.FragmentProductDetailsBrandProductListBinding
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner.AvailableProductsAdapter
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener

class DetailBrandListViewHolder(
    view: View,
    val clickListener: ProductDetailsClickListener,
    productsClickListener: ProductsClickListener,
    likeManager: LikeManager,
    cartManager: CartManager,
    ratingProductManager: RatingProductManager
) : ItemViewHolder<DetailBrandList>(view) {


    private val binding: FragmentProductDetailsBrandProductListBinding = FragmentProductDetailsBrandProductListBinding.bind(view)

    internal val space: Int by lazy { itemView.context.resources.getDimension(R.dimen.space_16).toInt() }

    private val productsAdapter = AvailableProductsAdapter(productsClickListener, likeManager, cartManager, ratingProductManager)

    init {
        binding.nextPage.setOnClickListener { clickListener.onNextPageBrandProductsClick(bindingAdapterPosition) }

        binding.brandProductRecycler.layoutManager = LinearLayoutManager(itemView.context)
        //binding.brandProductRecycler.adapter = productsAdapter
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

    override fun getState(): Parcelable? {
        return binding.brandProductRecycler.layoutManager?.onSaveInstanceState()
    }

    override fun setState(state: Parcelable) {
        binding.brandProductRecycler.layoutManager?.onRestoreInstanceState(state)
    }

    override fun bind(item: DetailBrandList) {
        super.bind(item)

        productsAdapter.submitList(item.productUiList, "")

        //if (item.pageAmount <= 1) {
        //    binding.nextPage.visibility = View.GONE
        //} else {
        //    binding.nextPage.visibility = View.VISIBLE
        //}
    }
}