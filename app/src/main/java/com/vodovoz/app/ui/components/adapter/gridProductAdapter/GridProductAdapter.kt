package com.vodovoz.app.ui.components.adapter.gridProductAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderProductGridBinding
import com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.grid.ProductGridViewHolder
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class GridProductAdapter(
    private val onProductClickSubject: PublishSubject<Long>,
) : RecyclerView.Adapter<ProductGridViewHolder>() {

    var productUiList = listOf<ProductUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ProductGridViewHolder(
        binding = ViewHolderProductGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onProductClickSubject = onProductClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: ProductGridViewHolder,
        position: Int
    ) = holder.onBind(productUiList[position])

    override fun getItemCount() = productUiList.size

}