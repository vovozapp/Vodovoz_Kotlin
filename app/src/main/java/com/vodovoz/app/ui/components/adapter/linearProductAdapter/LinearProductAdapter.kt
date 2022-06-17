package com.vodovoz.app.ui.components.adapter.linearProductAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderProductListBinding
import com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.list.ProductListViewHolder
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class LinearProductAdapter(
    private val onProductClickSubject: PublishSubject<Long>,
) : RecyclerView.Adapter<ProductListViewHolder>() {

    var productUiList = listOf<ProductUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ProductListViewHolder(
        binding = ViewHolderProductListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onProductClickSubject = onProductClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: ProductListViewHolder,
        position: Int
    ) = holder.onBind(productUiList[position])

    override fun getItemCount() = productUiList.size

}