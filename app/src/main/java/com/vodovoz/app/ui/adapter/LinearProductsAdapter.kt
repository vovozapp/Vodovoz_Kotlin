package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderProductListBinding
import com.vodovoz.app.ui.view_holder.ProductListViewHolder
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class LinearProductsAdapter(
    private val onProductClick: (Long) -> Unit,
    private val onChangeCartQuantity: (Long, Int) -> Unit,
    private val onChangeFavoriteStatus: (Long, Boolean) -> Unit,
    private val onNotifyWhenBeAvailable: (Long) -> Unit,
    private val onNotAvailableMore: () -> Unit,
    private val isCart: Boolean = false
) : RecyclerView.Adapter<ProductListViewHolder>() {

    var productUIList = listOf<ProductUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ProductListViewHolder(
        binding = ViewHolderProductListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        isCartItem = isCart,
        onProductClick = onProductClick,
        onChangeCartQuantity = onChangeCartQuantity,
        onChangeFavoriteStatus = onChangeFavoriteStatus,
        onNotifyWhenBeAvailable = onNotifyWhenBeAvailable,
        onNotAvailableMore = onNotAvailableMore,
    )

    override fun onBindViewHolder(
        holder: ProductListViewHolder,
        position: Int
    ) = holder.onBind(productUIList[position])

    override fun getItemCount() = productUIList.size

}