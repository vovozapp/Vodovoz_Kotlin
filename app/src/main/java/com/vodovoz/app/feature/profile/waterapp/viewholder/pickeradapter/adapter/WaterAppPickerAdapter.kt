package com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppInnerClickListener
import com.vodovoz.app.feature.profile.waterapp.viewholder.*
import com.vodovoz.app.feature.profile.waterapp.viewholder.inner.*
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.viewholder.*

class WaterAppPickerAdapter(
    private val waterAppHelper: WaterAppHelper,
    private val clickListener: WaterAppClickListener,
    private val innerClickListener: WaterAppInnerClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            1 -> { HeightViewHolder(getViewFromInflater(R.layout.item_picker, parent), clickListener, waterAppHelper, innerClickListener) }
            2 -> { WeightViewHolder(getViewFromInflater(R.layout.item_picker, parent), clickListener, waterAppHelper, innerClickListener) }
            3 -> { SleepTimeViewHolder(getViewFromInflater(R.layout.item_picker, parent), clickListener, waterAppHelper, innerClickListener) }
            4 -> { WakeTimeViewHolder(getViewFromInflater(R.layout.item_picker, parent), clickListener, waterAppHelper, innerClickListener) }
            5 -> { DurationViewHolder(getViewFromInflater(R.layout.item_water_app_duration, parent), clickListener, waterAppHelper, innerClickListener) }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}