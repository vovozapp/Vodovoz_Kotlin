package com.vodovoz.app.feature.profile.waterapp.viewholder

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppSeventhBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppInnerClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppLists
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelSeven
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.adapter.WaterAppPickerAdapter
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.launch

class WaterAppViewHolderSeventh(
    view: View,
    clickListener: WaterAppClickListener,
    private val waterAppHelper: WaterAppHelper,
    innerClickListener: WaterAppInnerClickListener,
) : ItemViewHolder<WaterAppModelSeven>(view) {

    private val binding: FragmentWaterAppSeventhBinding = FragmentWaterAppSeventhBinding.bind(view)

    private val layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)

    private val pickerAdapter = WaterAppPickerAdapter(waterAppHelper, clickListener, innerClickListener).apply { submitList(
        WaterAppLists.durationLists) }

    override fun attach() {
        super.attach()

        launch {
            waterAppHelper
                .observeWaterAppNotificationData()
                .collect {
                    if (it == null) return@collect
                    debugLog { "notification switch ${it.switch}" }
                    binding.sw.isChecked = it.switch
                    binding.rvItems.isVisible = it.switch
                }
        }

        waterAppHelper.saveNotificationFirstShow()
    }

    init {
        binding.rvItems.layoutManager = layoutManager
        binding.rvItems.adapter = pickerAdapter

        binding.tvConfirm.setOnClickListener {
            val item = item ?: return@setOnClickListener
            waterAppHelper.saveWaterAppNotificationData()
            if (item.id == 5) {
                clickListener.onNextClick(item.id)
            } else {
                clickListener.onPrevClick(item.id)
            }
        }

        binding.sw.setOnCheckedChangeListener { _, b ->
            waterAppHelper.saveNotificationSwitch(b)
        }
    }

    override fun bind(item: WaterAppModelSeven) {
        super.bind(item)


    }
}