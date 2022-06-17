package com.vodovoz.app.ui.components.adapter.pagingProductsAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderProductGridBinding
import com.vodovoz.app.databinding.ViewHolderProductListBinding
import com.vodovoz.app.ui.components.fragment.products.ViewMode
import com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.grid.ProductGridViewHolder
import com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.list.ProductListViewHolder
import com.vodovoz.app.ui.components.diffUtils.ProductDiffItemCallback
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class ProductsAdapter(
    private val onProductClickSubject: PublishSubject<Long>,
    private val productDiffItemCallback: ProductDiffItemCallback,
    var viewMode: ViewMode
) : PagingDataAdapter<ProductUI, RecyclerView.ViewHolder>(productDiffItemCallback) {

    override fun getItemViewType(position: Int) = viewMode.code

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (viewType) {
        ViewMode.LIST.code -> ProductListViewHolder(
            binding = ViewHolderProductListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            context = parent.context,
            onProductClickSubject = onProductClickSubject
        )
        ViewMode.GRID.code -> ProductGridViewHolder(
            binding = ViewHolderProductGridBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            context = parent.context,
            onProductClickSubject = onProductClickSubject
        )
        else -> throw Exception()
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) = when (getItemViewType(position)) {
        ViewMode.LIST.code -> (holder as ProductListViewHolder).onBind(getItem(position)!!)
        ViewMode.GRID.code -> (holder as ProductGridViewHolder).onBind(getItem(position)!!)
        else -> throw Exception()
    }

}