package com.vodovoz.app.feature.profile.waterapp.viewholder

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppFirstBinding
import com.vodovoz.app.databinding.FragmentWaterAppSecondBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelOne
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelTwo

class WaterAppViewHolderSecond(
    view: View,
    clickListener: WaterAppClickListener,
    waterAppHelper: WaterAppHelper
) : ItemViewHolder<WaterAppModelTwo>(view) {

    private val binding: FragmentWaterAppSecondBinding = FragmentWaterAppSecondBinding.bind(view)

    init {
        binding.cwNext.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onNextClick(item.id)
        }
    }

    override fun bind(item: WaterAppModelTwo) {
        super.bind(item)


    }
}