package com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.viewholder

import android.view.View
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemWaterAppDurationBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppInnerClickListener
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model.PickerDuration

class DurationViewHolder(
    view: View,
    clickListener: WaterAppClickListener,
    private val waterAppHelper: WaterAppHelper,
    private val innerClickListener: WaterAppInnerClickListener,
) : ItemViewHolder<PickerDuration>(view) {

    private val binding: ItemWaterAppDurationBinding = ItemWaterAppDurationBinding.bind(view)

    init {

    }

    override fun attach() {
        super.attach()

    }

    override fun bind(item: PickerDuration) {
        super.bind(item)

    }


    fun select(select: Boolean, time: String) {
        if (select) {
            binding.tvValue.setBackgroundResource(R.drawable.bkg_blue_button)
        } else {
            binding.tvValue.setBackgroundResource(R.drawable.bkg_gray_button)
        }
        binding.tvValue.text = time
    }
}