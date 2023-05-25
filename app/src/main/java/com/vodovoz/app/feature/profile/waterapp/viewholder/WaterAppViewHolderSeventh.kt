package com.vodovoz.app.feature.profile.waterapp.viewholder

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppSeventhBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelSeven

class WaterAppViewHolderSeventh(
    view: View,
    clickListener: WaterAppClickListener,
    waterAppHelper: WaterAppHelper
) : ItemViewHolder<WaterAppModelSeven>(view) {

    private val binding: FragmentWaterAppSeventhBinding = FragmentWaterAppSeventhBinding.bind(view)

    init {
        binding.tvConfirm.setOnClickListener {
            val item = item ?: return@setOnClickListener
            if (item.id == 5) {
                clickListener.onNextClick(item.id)
            } else {
                clickListener.onPrevClick(item.id)
            }
        }
    }

    override fun bind(item: WaterAppModelSeven) {
        super.bind(item)


    }
}