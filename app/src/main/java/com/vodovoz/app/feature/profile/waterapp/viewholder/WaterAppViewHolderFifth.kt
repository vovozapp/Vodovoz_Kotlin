package com.vodovoz.app.feature.profile.waterapp.viewholder

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppFifthBinding
import com.vodovoz.app.databinding.FragmentWaterAppFirstBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelFive
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelOne

class WaterAppViewHolderFifth(
    view: View,
    clickListener: WaterAppClickListener,
    waterAppHelper: WaterAppHelper
) : ItemViewHolder<WaterAppModelFive>(view) {

    private val binding: FragmentWaterAppFifthBinding = FragmentWaterAppFifthBinding.bind(view)

    init {
        binding.imgSettings.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onNextClick(item.id)
        }
    }

    override fun bind(item: WaterAppModelFive) {
        super.bind(item)


    }
}