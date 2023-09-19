package com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemPickerBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppInnerClickListener
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model.PickerWakeTime
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.launch

class WakeTimeViewHolder(
    view: View,
    clickListener: WaterAppClickListener,
    private val waterAppHelper: WaterAppHelper,
    private val innerClickListener: WaterAppInnerClickListener
) : ItemViewHolder<PickerWakeTime>(view) {

    private val binding: ItemPickerBinding = ItemPickerBinding.bind(view)

    override fun attach() {
        super.attach()
        launch {
            waterAppHelper
                .observeWaterAppUserData()
                .collect {
                    if (it == null || it.wakeUpTime.isEmpty()) return@collect
                    val item = item ?: return@collect

                    val t = item.id/10 + 10

                    debugLog { "wakeUpTime ${it.wakeUpTime} id ${item.id} t $t" }

                    if (t == it.wakeUpTime.toInt()) {
                        binding.tvValue.setTextColor(ContextCompat.getColor(itemView.context, R.color.bluePrimary))
                    } else {
                        binding.tvValue.setTextColor(ContextCompat.getColor(itemView.context, R.color.gray_unselected))
                    }
                }
        }
    }

    override fun bind(item: PickerWakeTime) {
        super.bind(item)

        binding.tvValue.text = fetchTimeString(item.id)
    }

    private fun fetchTimeString(id: Int) : String {
        val hour: Int = id / 60
        val minutes: Int = id % 60
        val hourStr: String = if (hour < 10) "0$hour" else hour.toString()
        val minutesStr = if (minutes < 10) "0$minutes" else  minutes.toString()
        return "$hourStr:$minutesStr"
    }
}