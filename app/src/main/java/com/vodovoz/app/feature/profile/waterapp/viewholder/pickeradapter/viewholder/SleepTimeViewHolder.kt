package com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemPickerBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model.PickerSleepTime
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.launch

class SleepTimeViewHolder(
    view: View,
    private val waterAppHelper: WaterAppHelper,
) : ItemViewHolder<PickerSleepTime>(view) {

    private val binding: ItemPickerBinding = ItemPickerBinding.bind(view)

    override fun attach() {
        super.attach()
        launch {
            waterAppHelper
                .observeWaterAppUserData()
                .collect {
                    if (it == null || it.sleepTime.isEmpty()) return@collect
                    val item = item ?: return@collect

                    val t = item.id / 10 + 10

                    debugLog { "sleepTime ${it.sleepTime} id ${item.id} t $t" }

                    if (t == it.sleepTime.toInt()) {
                        binding.tvValue.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.bluePrimary
                            )
                        )
                    } else {
                        binding.tvValue.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.gray_unselected
                            )
                        )
                    }
                }
        }
    }

    override fun bind(item: PickerSleepTime) {
        super.bind(item)

        binding.tvValue.text = fetchTimeString(item.id)
    }

    private fun fetchTimeString(id: Int): String {
        val hour: Int = id / 60
        val minutes: Int = id % 60
        val hourStr: String = if (hour < 10) "0$hour" else hour.toString()
        val minutesStr = if (minutes < 10) "0$minutes" else minutes.toString()
        return "$hourStr:$minutesStr"
    }
}