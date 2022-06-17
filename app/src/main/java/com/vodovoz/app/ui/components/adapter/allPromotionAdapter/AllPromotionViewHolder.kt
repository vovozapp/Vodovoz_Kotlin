package com.vodovoz.app.ui.components.adapter.allPromotionAdapter

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderPromotionBinding
import com.vodovoz.app.databinding.ViewHolderSliderPromotionBinding
import com.vodovoz.app.ui.components.adapter.promotionProductAdapter.PromotionProductDiffUtilCallback
import com.vodovoz.app.ui.components.adapter.promotionProductAdapter.PromotionProductSliderAdapter
import com.vodovoz.app.ui.model.PromotionUI
import io.reactivex.rxjava3.subjects.PublishSubject

class AllPromotionViewHolder(
    private val binding: ViewHolderPromotionBinding,
    private val onPromotionClickSubject: PublishSubject<Long>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
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
    }
}