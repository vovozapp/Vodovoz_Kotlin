package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderCartItemNotAvailableBinding
import com.vodovoz.app.ui.view_holder.CartItemNotAvailableViewHolder
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class NotAvailableCartItemsAdapter(
    private val onProductClickSubject: PublishSubject<Long>,
    private val onSwapClickSubject: PublishSubject<Long>
) : RecyclerView.Adapter<CartItemNotAvailableViewHolder>() {

    var productUIList = listOf<ProductUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = CartItemNotAvailableViewHolder(
        binding = ViewHolderCartItemNotAvailableBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onProductClickSubject = onProductClickSubject,
        onSwapClickSubject = onSwapClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: CartItemNotAvailableViewHolder,
        position: Int
    ) = holder.onBind(productUIList[position])

    override fun getItemCount() = productUIList.size

}