package com.vodovoz.app.feature.profile.waterapp.viewholder

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppFirstBinding
import com.vodovoz.app.databinding.FragmentWaterAppSixthBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelOne
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelSix

class WaterAppViewHolderSixth(
    view: View,
    clickListener: WaterAppClickListener,
    waterAppHelper: WaterAppHelper
) : ItemViewHolder<WaterAppModelSix>(view) {

    private val binding: FragmentWaterAppSixthBinding = FragmentWaterAppSixthBinding.bind(view)

    init {
        binding.imgBack.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onPrevClick(item.id)
        }

        binding.tvAlertSettings.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onNextClick(item.id)
        }

        binding.tvResetRate.setOnClickListener {
            waterAppHelper.clearData()
            clickListener.clearData()
        }

    }

    override fun bind(item: WaterAppModelSix) {
        super.bind(item)


    }
}