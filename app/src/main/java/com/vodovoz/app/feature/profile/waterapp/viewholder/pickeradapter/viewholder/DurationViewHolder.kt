package com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.viewholder

import android.view.View
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemWaterAppDurationBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model.PickerDuration
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.launch

class DurationViewHolder(
    view: View,
    private val waterAppHelper: WaterAppHelper,
) : ItemViewHolder<PickerDuration>(view) {

    private val binding: ItemWaterAppDurationBinding = ItemWaterAppDurationBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            waterAppHelper.saveNotificationTime(item.id.toString())
        }

    }

    override fun attach() {
        super.attach()

        launch {
            waterAppHelper
                .observeWaterAppNotificationData()
                .collect {
                    if (it == null) return@collect
                    debugLog { "notification switch ${it.time}" }
                    checkTime(it.time)
                }
        }
    }

    override fun bind(item: PickerDuration) {
        super.bind(item)

    }


    private fun checkTime(time: String) {
        val item = item ?: return

        when (item.id) {
            15 -> {
                select(item.id == time.toInt(), "15 минут")
            }
            30 -> {
                select(item.id == time.toInt(), "30 минут")
            }
            45 -> {
                select(item.id == time.toInt(), "45 минут")
            }
            60 -> {
                select(item.id == time.toInt(), "1 час")
            }
            90 -> {
                select(item.id == time.toInt(), "1.5 часа")
            }
            120 -> {
                select(item.id == time.toInt(), "2 часа")
            }
            150 -> {
                select(item.id == time.toInt(), "2.5 часа")
            }
            180 -> {
                select(item.id == time.toInt(), "3 часа")
            }
            210 -> {
                select(item.id == time.toInt(), "3.5 часа")
            }
            240 -> {
                select(item.id == time.toInt(), "4 часа")
            }
        }
    }

    private fun select(select: Boolean, time: String) {
        if (select) {
            binding.tvValue.setBackgroundResource(R.drawable.bkg_blue_button)
        } else {
            binding.tvValue.setBackgroundResource(R.drawable.bkg_gray_button)
        }
        binding.tvValue.text = time
    }
}