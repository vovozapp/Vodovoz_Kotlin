package com.vodovoz.app.ui.view_holder

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderPromotionBinding
import com.vodovoz.app.ui.model.PromotionUI
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionWithoutProductsViewHolder(
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

        binding.tvName.text = promotionUI.name
        binding.tvTimeLeft.text = promotionUI.timeLeft
        when(promotionUI.customerCategory) {
            null -> binding.tvCustomerCategory.visibility = View.GONE
            else -> binding.tvCustomerCategory.text = promotionUI.customerCategory
        }
        promotionUI.statusColor?.let { noNullColor ->
            binding.cwCustomerCategory.setCardBackgroundColor(Color.parseColor(noNullColor))
        }

        Glide
            .with(context)
            .load(promotionUI.detailPicture)
            .into(binding.imgImage)
    }
}