package com.vodovoz.app.ui.view_holder

import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPromotionFilterBinding
import com.vodovoz.app.ui.model.PromotionFilterUI

class PromotionFilterViewHolder(
    private val binding: ViewHolderPromotionFilterBinding,
    private val onPromotionFilterClickListener: (Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onPromotionFilterClickListener(promotionFilterUI.id)
        }
    }

    private lateinit var promotionFilterUI: PromotionFilterUI

    fun onBind(promotionFilterUI: PromotionFilterUI, isSelected: Boolean) {
        this.promotionFilterUI = promotionFilterUI
        binding.root.text = promotionFilterUI.name
        when (isSelected) {
            true -> binding.root.setTypeface(null, Typeface.BOLD)
            false -> binding.root.setTypeface(null, Typeface.NORMAL)
        }
    }
}