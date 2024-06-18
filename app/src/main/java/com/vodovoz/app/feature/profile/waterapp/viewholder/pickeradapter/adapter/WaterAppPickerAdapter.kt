package com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.viewholder.DurationViewHolder
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.viewholder.HeightViewHolder
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.viewholder.SleepTimeViewHolder
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.viewholder.WakeTimeViewHolder
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.viewholder.WeightViewHolder

class WaterAppPickerAdapter(
    private val waterAppHelper: WaterAppHelper,
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            1 -> {
                HeightViewHolder(getViewFromInflater(R.layout.item_picker, parent), waterAppHelper)
            }
            2 -> {
                WeightViewHolder(getViewFromInflater(R.layout.item_picker, parent), waterAppHelper)
            }
            3 -> {
                SleepTimeViewHolder(
                    getViewFromInflater(R.layout.item_picker, parent),
                    waterAppHelper
                )
            }
            4 -> {
                WakeTimeViewHolder(
                    getViewFromInflater(R.layout.item_picker, parent),
                    waterAppHelper
                )
            }
            5 -> {
                DurationViewHolder(
                    getViewFromInflater(R.layout.item_water_app_duration, parent),
                    waterAppHelper
                )
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}