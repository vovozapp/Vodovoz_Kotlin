package com.vodovoz.app.feature.home.viewholders.homeproducts

import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentSliderProductBinding
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.HomeCategoriesInnerAdapter
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.HomeProductsInnerAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.decoration.GridMarginDecoration
import com.vodovoz.app.ui.decoration.ProductSliderMarginDecoration

class HomeProductsSliderViewHolder(
    view: View,
    private val productsShowAllListener: ProductsShowAllListener,
    productsClickListener: ProductsClickListener,
    cartManager: CartManager,
    likeManager: LikeManager,
    linear: Boolean = true
) : ItemViewHolder<HomeProducts>(view) {

    private val binding: FragmentSliderProductBinding = FragmentSliderProductBinding.bind(view)

    private val homeProductsAdapter = HomeProductsInnerAdapter(productsClickListener, cartManager, likeManager)

    private var isAddItemDecoration = false

//    private val space: Int by lazy { itemView.context.resources.getDimension(R.dimen.space_16).toInt() }

    private val gridMarginDecoration: GridMarginDecoration by lazy {
        GridMarginDecoration(16)
    }

    init {
        binding.rvCategories.layoutManager = if(linear) {
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        } else {
            GridLayoutManager(itemView.context, 2)
        }
//        if(!linear){
//            binding.rvCategories.addItemDecoration(gridMarginDecoration)
//        }

        binding.rvCategories.adapter = homeProductsAdapter
        binding.rvCategories.setRecycledViewPool(likeManager.fetchViewPool())
        binding.rvCategories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onScrollInnerRecycler(this@HomeProductsSliderViewHolder)
            }
        })
    }

    override fun getState(): Parcelable? {
        return binding.rvCategories.layoutManager?.onSaveInstanceState()
    }

    override fun setState(state: Parcelable) {
        binding.rvCategories.layoutManager?.onRestoreInstanceState(state)
    }

    override fun bind(item: HomeProducts) {
        super.bind(item)

        homeProductsAdapter.submitList(item.prodList)

        if (!isAddItemDecoration) {
            binding.rvCategories.addItemDecoration(
                ProductSliderMarginDecoration(
                    space = itemView.context.resources.getDimension(R.dimen.space_16).toInt(),
                    itemCount = item.items.first().productUIList.size,
                    isLast = bindingAdapterPosition == (bindingAdapter as? HomeCategoriesInnerAdapter)?.itemCount?.minus(1)
                )
            )
            isAddItemDecoration = true
        }
    }
}