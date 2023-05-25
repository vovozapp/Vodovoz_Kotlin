package com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.viewholder

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppFifthBinding
import com.vodovoz.app.databinding.ItemPickerBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppInnerClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelFive
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model.PickerHeight
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model.PickerSleepTime

class SleepTimeViewHolder(
    view: View,
    clickListener: WaterAppClickListener,
    waterAppHelper: WaterAppHelper,
    private val innerClickListener: WaterAppInnerClickListener
) : ItemViewHolder<PickerSleepTime>(view) {

    private val binding: ItemPickerBinding = ItemPickerBinding.bind(view)

    init {

    }

    override fun bind(item: PickerSleepTime) {
        super.bind(item)

    }
}