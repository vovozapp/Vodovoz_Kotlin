package com.vodovoz.app.feature.all.orders

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderOrderDetailsBinding
import com.vodovoz.app.feature.all.AllClickListener
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowClickListener
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureFlowPagerAdapter
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPictureSlider
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.OrderUI
import com.vodovoz.app.util.extensions.color

class AllOrdersViewHolder(
    view: View,
    private val allClickListener: AllClickListener,
) : ItemViewHolder<OrderUI>(view) {

    private val binding: ViewHolderOrderDetailsBinding = ViewHolderOrderDetailsBinding.bind(view)

    private val detailPictureFlowPagerAdapter = DetailPictureFlowPagerAdapter(
        clickListener = object : DetailPictureFlowClickListener {
            override fun onProductClick(id: Long) {
                allClickListener.onProductDetailPictureClick(id)
            }
        }
    )

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

        binding.rvProducts.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvProducts.adapter = detailPictureFlowPagerAdapter
    }

    override fun bind(item: OrderUI) {
        super.bind(item)

        binding.tvStatus.text = item.orderStatusUI?.statusName
        binding.tvAddress.text = item.address
        binding.tvPrice.setPriceText(item.price)
        binding.tvStatus.setTextColor(itemView.context.color(item.orderStatusUI?.color?: R.color.color_transparent))
        binding.imgStatus.setImageDrawable(item.orderStatusUI?.image?.let { ContextCompat.getDrawable(itemView.context, it) })
        binding.imgStatus.setColorFilter(itemView.context.color(item.orderStatusUI?.color?: R.color.color_transparent))

        val newDetailPictureList = mutableListOf<DetailPictureSlider>().apply {
            item.productUIList.forEach { add(DetailPictureSlider(it.id, it.detailPicture, it.isAvailable)) }
        }.toList()

        detailPictureFlowPagerAdapter.submitList(newDetailPictureList)

    }
}