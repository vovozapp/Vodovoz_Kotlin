package com.vodovoz.app.ui.view_holder

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderSliderPromotionBinding
import com.vodovoz.app.ui.adapter.PromotionProductsSliderAdapter
import com.vodovoz.app.ui.diffUtils.PromotionProductDiffUtilCallback
import com.vodovoz.app.ui.model.PromotionUI
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionSliderViewHolder(
    private val binding: ViewHolderSliderPromotionBinding,
    private val onPromotionClickSubject: PublishSubject<Long>,
    private val onProductClickSubject: PublishSubject<Long>,
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>>,
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private val promotionProductsSliderAdapter = PromotionProductsSliderAdapter(
        onProductClickSubject = onProductClickSubject,
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
        onFavoriteClickSubject = onFavoriteClickSubject
    )

    init {
        binding.rvProducts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvProducts.setHasFixedSize(true)
        val space = context.resources.getDimension(R.dimen.space_16).toInt()
        binding.rvProducts.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        left = space / 2
                        top = space / 2
                        right = space / 2
                        bottom = space / 2
                    }
                }
            }
        )
        binding.rvProducts.adapter = promotionProductsSliderAdapter

        binding.root.setOnClickListener { onPromotionClickSubject.onNext(promotionUI.id) }
    }

    private lateinit var promotionUI: PromotionUI

    fun onBind(promotionUI: PromotionUI) {
        this.promotionUI = promotionUI

        binding.tvName.text = promotionUI.name
        binding.tvTimeLeft.text = promotionUI.timeLeft
        binding.tvCustomerCategory.text = promotionUI.customerCategory
        binding.cwCustomerCategory.setCardBackgroundColor(Color.parseColor(promotionUI.statusColor))

        Glide
            .with(context)
            .load(promotionUI.detailPicture)
            .into(binding.imgImage)

        val diffUtil = PromotionProductDiffUtilCallback(
            newList = promotionUI.productUIList,
            oldList = promotionProductsSliderAdapter.productUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            promotionProductsSliderAdapter.productUIList = promotionUI.productUIList
            diffResult.dispatchUpdatesTo(promotionProductsSliderAdapter)
        }
    }
}