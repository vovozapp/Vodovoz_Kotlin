package com.vodovoz.app.feature.all.orders

import android.view.View
import androidx.core.content.ContextCompat
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderOrderDetailsBinding
import com.vodovoz.app.feature.all.AllClickListener
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.OrderUI
import com.vodovoz.app.util.extensions.color

class AllOrdersViewHolder(
    view: View,
    private val allClickListener: AllClickListener,
) : ItemViewHolder<OrderUI>(view) {

    private val binding: ViewHolderOrderDetailsBinding = ViewHolderOrderDetailsBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val itemId = item?.id ?: return@setOnClickListener
            allClickListener.onMoreDetailClick(itemId)
        }

        binding.tvMoreDetails.setOnClickListener {
            val itemId = item?.id ?: return@setOnClickListener
            allClickListener.onMoreDetailClick(itemId)
        }
        binding.tvRepeatOrder.setOnClickListener {
            val itemId = item?.id ?: return@setOnClickListener
            allClickListener.onRepeatOrderClick(itemId)
        }
    }

    override fun bind(item: OrderUI) {
        super.bind(item)

        binding.tvStatus.text = item.orderStatusUI?.statusName
        binding.tvAddress.text = item.address
        binding.tvPrice.setPriceText(item.price)
        binding.tvStatus.setTextColor(itemView.context.color(item.orderStatusUI?.color?: R.color.color_transparent))
        binding.imgStatus.setImageDrawable(item.orderStatusUI?.image?.let { ContextCompat.getDrawable(itemView.context, it) })
        binding.imgStatus.setColorFilter(itemView.context.color(item.orderStatusUI?.color?: R.color.color_transparent))

        val newDetailPictureList = mutableListOf<Triple<Long, String, Boolean>>().apply {
            item.productUIList.forEach { add(Triple(it.id, it.detailPicture, it.isAvailable)) }
        }.toList()


    }
}