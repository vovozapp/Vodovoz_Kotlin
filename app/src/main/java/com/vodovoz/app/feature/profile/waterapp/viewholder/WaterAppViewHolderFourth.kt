package com.vodovoz.app.feature.profile.waterapp.viewholder

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppFirstBinding
import com.vodovoz.app.databinding.FragmentWaterAppFourthBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelFour
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelOne

class WaterAppViewHolderFourth(
    view: View,
    clickListener: WaterAppClickListener,
    private val waterAppHelper: WaterAppHelper
) : ItemViewHolder<WaterAppModelFour>(view) {

    private val binding: FragmentWaterAppFourthBinding = FragmentWaterAppFourthBinding.bind(view)

    init {
        binding.cwStart.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onNextClick(item.id)
        }
    }

    override fun attach() {
        super.attach()
        val rate = waterAppHelper.fetchRate()

        binding.tvYourRate.text = "$rate мл"
    }

    override fun bind(item: WaterAppModelFour) {
        super.bind(item)


    }
}