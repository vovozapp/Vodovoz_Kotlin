package com.vodovoz.app.ui.view_holder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderOrderDetailsBinding
import com.vodovoz.app.ui.adapter.DetailPictureSliderAdapter
import com.vodovoz.app.ui.diffUtils.DetailPictureDiffUtilCallback
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.OrderUI
import io.reactivex.rxjava3.subjects.PublishSubject

class OrderDetailViewHolder(
    private val binding: ViewHolderOrderDetailsBinding,
    private val onMoreDetailClickSubject: PublishSubject<Long>,
    private val onRepeatOrderClickSubject: PublishSubject<Long>,
    private val onProductDetailPictureClickSubject: PublishSubject<Long>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private val detailPictureSliderAdapter = DetailPictureSliderAdapter(
        onProductDetailPictureClickSubject = onProductDetailPictureClickSubject
    )

    init {
        binding.root.setOnClickListener { onMoreDetailClickSubject.onNext(orderUI.id!!) }
        binding.tvMoreDetails.setOnClickListener { onMoreDetailClickSubject.onNext(orderUI.id!!) }
        binding.tvRepeatOrder.setOnClickListener { onRepeatOrderClickSubject.onNext(orderUI.id!!) }

        binding.rvProducts.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvProducts.adapter = detailPictureSliderAdapter
    }

    private lateinit var orderUI: OrderUI

    fun onBind(orderUI: OrderUI) {
        this.orderUI = orderUI

        binding.tvStatus.text = orderUI.orderStatusUI?.statusName
        binding.tvAddress.text = orderUI.address
        binding.tvPrice.setPriceText(orderUI.price!!)
        binding.tvStatus.setTextColor(ContextCompat.getColor(context, orderUI.orderStatusUI!!.color))
        binding.imgStatus.setImageDrawable(ContextCompat.getDrawable(context, orderUI.orderStatusUI.image))
        binding.imgStatus.setColorFilter(ContextCompat.getColor(context, orderUI.orderStatusUI.color))

        val newDetailPictureList = mutableListOf<Pair<Long, String>>().apply {
            orderUI.productUIList.forEach { add(Pair(it.id, it.detailPicture)) }
        }.toList()

        val diffUtil = DetailPictureDiffUtilCallback(
            oldList = mutableListOf<String>().apply {
                detailPictureSliderAdapter.detailPictureUrlList.forEach { add(it.second) }
            },
            newList = mutableListOf<String>().apply {
                orderUI.productUIList.forEach { add(it.detailPicture) }
            }
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            detailPictureSliderAdapter.detailPictureUrlList = newDetailPictureList
            diffResult.dispatchUpdatesTo(detailPictureSliderAdapter)
        }
    }

}