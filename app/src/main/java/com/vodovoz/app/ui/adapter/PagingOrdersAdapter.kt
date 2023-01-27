package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.vodovoz.app.databinding.ViewHolderOrderDetailsBinding
import com.vodovoz.app.ui.diffUtils.OrderDiffItemCallback
import com.vodovoz.app.ui.model.OrderUI
import com.vodovoz.app.ui.view_holder.OrderDetailViewHolder
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