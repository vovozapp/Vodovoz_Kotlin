package com.vodovoz.app.ui.components.adapter.promotionSliderAdapter

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderPromotionBinding
import com.vodovoz.app.ui.components.adapter.promotionProductAdapter.PromotionProductDiffUtilCallback
import com.vodovoz.app.ui.components.adapter.promotionProductAdapter.PromotionProductSliderAdapter
import com.vodovoz.app.ui.model.PromotionUI
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionSliderViewHolder(
    private val binding: ViewHolderSliderPromotionBinding,
    private val onPromotionClickSubject: PublishSubject<Long>,
    private val onProductClickSubject: PublishSubject<Long>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private val promotionProductSliderAdapter = PromotionProductSliderAdapter(onProductClickSubject)

    init {
        binding.productPager.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.productPager.setHasFixedSize(true)
        binding.productPager.adapter = promotionProductSliderAdapter

        binding.root.setOnClickListener { onPromotionClickSubject.onNext(promotionUI.id) }
    }

    private lateinit var promotionUI: PromotionUI

    fun onBind(promotionUI: PromotionUI) {
        this.promotionUI = promotionUI

        binding.title.text = promotionUI.name
        binding.timeLeft.text = promotionUI.timeLeft
        binding.customerCategory.text = promotionUI.customerCategory
        binding.customerCategoryCard.setCardBackgroundColor(Color.parseColor(promotionUI.statusColor))

        Glide
            .with(context)
            .load(promotionUI.detailPicture)
            .into(binding.detailPicture)

        val diffUtil = PromotionProductDiffUtilCallback(
            newList = promotionUI.productUIList,
            oldList = promotionProductSliderAdapter.productUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            promotionProductSliderAdapter.productUIList = promotionUI.productUIList
            diffResult.dispatchUpdatesTo(promotionProductSliderAdapter)
        }
    }
}