package com.vodovoz.app.ui.view_holder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.PagerHowToOrderBinding
import com.vodovoz.app.ui.model.HowToOrderStepUI

class HowToOrderStepPage(
    private val binding: PagerHowToOrderBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(howToOrderStepUI: HowToOrderStepUI) {
        binding.stepTitle.text = howToOrderStepUI.stepTitle
        binding.stepDetails.text = howToOrderStepUI.stepDetails
        binding.stepImage.setImageDrawable(ContextCompat.getDrawable(context, howToOrderStepUI.stepImageResId))
    }

}