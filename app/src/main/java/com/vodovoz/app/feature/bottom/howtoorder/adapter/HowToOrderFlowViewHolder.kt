package com.vodovoz.app.feature.bottom.howtoorder.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.PagerHowToOrderBinding
import com.vodovoz.app.ui.model.HowToOrderStepUI

class HowToOrderFlowViewHolder(
    view: View,
) : ItemViewHolder<HowToOrderStepUI>(view) {

    private val binding: PagerHowToOrderBinding = PagerHowToOrderBinding.bind(view)

    override fun bind(item: HowToOrderStepUI) {
        super.bind(item)
        binding.stepTitle.text = item.stepTitle
        binding.stepDetails.text = item.stepDetails
        binding.stepImage.setImageDrawable(ContextCompat.getDrawable(itemView.context, item.stepImageResId))
    }
}