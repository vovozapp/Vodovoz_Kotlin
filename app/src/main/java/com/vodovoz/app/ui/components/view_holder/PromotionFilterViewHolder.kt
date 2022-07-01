package com.vodovoz.app.ui.components.view_holder

import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPromotionFilterBinding
import com.vodovoz.app.ui.model.PromotionFilterUI
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionFilterViewHolder(
    private val binding: ViewHolderPromotionFilterBinding,
    private val onPromotionFilterClickSubject: PublishSubject<Long>
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onPromotionFilterClickSubject.onNext(promotionFilterUI.id)
        }
    }

    private lateinit var promotionFilterUI: PromotionFilterUI

    fun onBind(promotionFilterUI: PromotionFilterUI, isSelected: Boolean) {
        this.promotionFilterUI = promotionFilterUI
        binding.root.text = promotionFilterUI.name
        when(isSelected) {
            true -> binding.root.setTypeface(null, Typeface.BOLD)
            false -> binding.root.setTypeface(null, Typeface.NORMAL)
        }

    }

}