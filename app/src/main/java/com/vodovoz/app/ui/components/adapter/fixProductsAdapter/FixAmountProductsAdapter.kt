package com.vodovoz.app.ui.components.adapter.fixProductsAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderProductGridBinding
import com.vodovoz.app.databinding.ViewHolderProductListBinding
import com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.grid.ProductGridViewHolder
import com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.list.ProductListViewHolder
import com.vodovoz.app.ui.components.diffUtils.ProductDiffItemCallback
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class FixAmountProductsAdapter(
    private val onProductClickSubject: PublishSubject<Long>
) : RecyclerView.Adapter<ProductListViewHolder>() {

    var productUIList = listOf<ProductUI>()

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
    ) = holder.onBind(productUIList[position])

    override fun getItemCount() = productUIList.size

}