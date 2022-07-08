package com.vodovoz.app.ui.components.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderOrderDetailsBinding
import com.vodovoz.app.databinding.ViewHolderProductGridBinding
import com.vodovoz.app.databinding.ViewHolderProductListBinding
import com.vodovoz.app.ui.components.diffUtils.OrderDiffItemCallback
import com.vodovoz.app.ui.components.diffUtils.OrderDiffUtilCallback
import com.vodovoz.app.ui.components.diffUtils.ProductDiffItemCallback
import com.vodovoz.app.ui.components.view_holder.OrderDetailViewHolder
import com.vodovoz.app.ui.components.view_holder.ProductGridViewHolder
import com.vodovoz.app.ui.components.view_holder.ProductListViewHolder
import com.vodovoz.app.ui.model.OrderUI
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class PagingOrdersAdapter(
    private val onMoreDetailClickSubject: PublishSubject<Long>,
    private val onRepeatOrderClickSubject: PublishSubject<Long>,
    private val onProductDetailPictureClickSubject: PublishSubject<Long>,
    private val orderDiffItemCallback: OrderDiffItemCallback
) : PagingDataAdapter<OrderUI, OrderDetailViewHolder>(orderDiffItemCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = OrderDetailViewHolder(
        binding = ViewHolderOrderDetailsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onMoreDetailClickSubject = onMoreDetailClickSubject,
        onRepeatOrderClickSubject = onRepeatOrderClickSubject,
        onProductDetailPictureClickSubject = onProductDetailPictureClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: OrderDetailViewHolder,
        position: Int
    ) = holder.onBind(getItem(position)!!)

}