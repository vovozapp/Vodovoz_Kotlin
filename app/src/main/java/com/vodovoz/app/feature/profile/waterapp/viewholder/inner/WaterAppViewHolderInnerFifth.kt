package com.vodovoz.app.feature.profile.waterapp.viewholder.inner

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppFirstBinding
import com.vodovoz.app.databinding.FragmentWaterAppInnerFifthBinding
import com.vodovoz.app.databinding.FragmentWaterAppInnerFirstBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelOne
import com.vodovoz.app.feature.profile.waterapp.model.inner.WaterAppModelInnerFive
import com.vodovoz.app.feature.profile.waterapp.model.inner.WaterAppModelInnerOne

class WaterAppViewHolderInnerFifth(
    view: View,
    clickListener: WaterAppClickListener,
    waterAppHelper: WaterAppHelper
) : ItemViewHolder<WaterAppModelInnerFive>(view) {

    private val binding: FragmentWaterAppInnerFifthBinding = FragmentWaterAppInnerFifthBinding.bind(view)

    init {

    }

    override fun bind(item: WaterAppModelInnerFive) {
        super.bind(item)


    }
}