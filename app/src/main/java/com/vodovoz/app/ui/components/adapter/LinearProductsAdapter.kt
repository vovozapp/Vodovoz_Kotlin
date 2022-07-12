package com.vodovoz.app.ui.components.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderProductListBinding
import com.vodovoz.app.ui.components.view_holder.ProductListViewHolder
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class LinearProductsAdapter(
    private val onProductClickSubject: PublishSubject<Long>,
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>>,
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>>
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
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
        onFavoriteClickSubject = onFavoriteClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: ProductListViewHolder,
        position: Int
    ) = holder.onBind(productUIList[position])

    override fun getItemCount() = productUIList.size

}