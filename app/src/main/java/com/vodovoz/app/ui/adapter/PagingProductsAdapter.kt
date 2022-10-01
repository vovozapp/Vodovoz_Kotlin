package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderProductGridBinding
import com.vodovoz.app.databinding.ViewHolderProductListBinding
import com.vodovoz.app.ui.diffUtils.ProductDiffItemCallback
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.view_holder.ProductGridViewHolder
import com.vodovoz.app.ui.view_holder.ProductListViewHolder

class PagingProductsAdapter(
    private val onProductClick: (Long) -> Unit,
    private val onChangeCartQuantity: (Long, Int) -> Unit,
    private val onChangeFavoriteStatus: (Long, Boolean) -> Unit,
    private val onNotifyWhenBeAvailable: (Long, String, String) -> Unit,
    private val onNotAvailableMore: () -> Unit,
    private val isCart: Boolean = false,
    private val productDiffItemCallback: ProductDiffItemCallback,
    var viewMode: ViewMode
) : PagingDataAdapter<ProductUI, RecyclerView.ViewHolder>(productDiffItemCallback) {

    override fun getItemViewType(position: Int) = viewMode.code

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (viewType) {
        ViewMode.LIST.code -> ProductListViewHolder(
            binding = ViewHolderProductListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            isCartItem = isCart,
            onProductClick = onProductClick,
            onChangeCartQuantity = onChangeCartQuantity,
            onChangeFavoriteStatus = onChangeFavoriteStatus,
            onNotifyWhenBeAvailable = onNotifyWhenBeAvailable,
            onNotAvailableMore = onNotAvailableMore
        )
        ViewMode.GRID.code -> ProductGridViewHolder(
            binding = ViewHolderProductGridBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onProductClick = onProductClick,
            onChangeCartQuantity = onChangeCartQuantity,
            onChangeFavoriteStatus = onChangeFavoriteStatus,
            onNotifyWhenBeAvailable = onNotifyWhenBeAvailable,
            onNotAvailableMore = onNotAvailableMore
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

    enum class ViewMode(val code: Int) {
        LIST(0), GRID(1)
    }

}