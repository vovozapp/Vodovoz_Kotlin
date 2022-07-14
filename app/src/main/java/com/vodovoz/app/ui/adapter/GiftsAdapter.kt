package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderGiftBinding
import com.vodovoz.app.ui.view_holder.GiftViewHolder
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class GiftsAdapter(
    private val onPickUpGiftSubject: PublishSubject<ProductUI>
) : RecyclerView.Adapter<GiftViewHolder>() {

    var productUIList = listOf<ProductUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = GiftViewHolder(
        binding = ViewHolderGiftBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onPickUpGiftSubject = onPickUpGiftSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: GiftViewHolder,
        position: Int
    ) = holder.onBind(productUIList[position])

    override fun getItemCount() = productUIList.size

}