package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderProductListNotAvailableBinding
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.view_holder.ProductListNotAvailableViewHolder

class NotAvailableCartItemsAdapter(
    private val onProductClick: (Long) -> Unit,
    private val onSwapProduct: (Long) -> Unit
) : RecyclerView.Adapter<ProductListNotAvailableViewHolder>() {

    var productUIList = listOf<ProductUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = ProductListNotAvailableViewHolder(
        binding = ViewHolderProductListNotAvailableBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onProductClick = onProductClick,
        onSwapProduct = onSwapProduct
    )

    override fun onBindViewHolder(
        holder: ProductListNotAvailableViewHolder,
        position: Int
    ) = holder.onBind(productUIList[position])

    override fun getItemCount() = productUIList.size

}