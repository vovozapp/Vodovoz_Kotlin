package com.vodovoz.app.ui.components.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderOrderDetailsBinding
import com.vodovoz.app.ui.components.interfaces.IOnProductDetailPictureClick
import com.vodovoz.app.ui.components.view_holder.OrderDetailViewHolder
import com.vodovoz.app.ui.model.OrderUI
import io.reactivex.rxjava3.subjects.PublishSubject

class OrdersDetailAdapter(
    private val onMoreDetailClickSubject: PublishSubject<Long>,
    private val onRepeatOrderClickSubject: PublishSubject<Long>,
    private val onProductDetailPictureClickSubject: PublishSubject<Long>,
) : RecyclerView.Adapter<OrderDetailViewHolder>() {

    var orderUIList = listOf<OrderUI>()

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
    ) = holder.onBind(orderUIList[position])

    override fun getItemCount() = orderUIList.size

}