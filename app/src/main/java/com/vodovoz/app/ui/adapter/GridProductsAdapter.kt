package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderProductGridBinding
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.view_holder.ProductGridViewHolder

class GridProductsAdapter(
    private val onProductClick: (Long) -> Unit,
    private val onChangeCartQuantity: (Long, Int) -> Unit,
    private val onChangeFavoriteStatus: (Long, Boolean) -> Unit,
    private val onNotifyWhenBeAvailable: (Long, String, String) -> Unit,
    private val onNotAvailableMore: () -> Unit
) : RecyclerView.Adapter<ProductGridViewHolder>() {

    var productUiList = listOf<ProductUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ProductGridViewHolder(
        binding = ViewHolderProductGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onProductClick = onProductClick,
        onChangeCartQuantity = onChangeCartQuantity,
        onChangeFavoriteStatus = onChangeFavoriteStatus,
        onNotifyWhenBeAvailable = onNotifyWhenBeAvailable,
        onNotAvailableMore = onNotAvailableMore
    )

    override fun onBindViewHolder(
        holder: ProductGridViewHolder,
        position: Int
    ) = holder.onBind(productUiList[position])

    override fun getItemCount() = productUiList.size

}