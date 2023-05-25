package com.vodovoz.app.feature.profile.waterapp.viewholder.inner

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppFirstBinding
import com.vodovoz.app.databinding.FragmentWaterAppInnerFirstBinding
import com.vodovoz.app.databinding.FragmentWaterAppInnerSixthBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppInnerClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelOne
import com.vodovoz.app.feature.profile.waterapp.model.inner.WaterAppModelInnerOne
import com.vodovoz.app.feature.profile.waterapp.model.inner.WaterAppModelInnerSix

class WaterAppViewHolderInnerSixth(
    view: View,
    clickListener: WaterAppClickListener,
    waterAppHelper: WaterAppHelper,
    private val innerClickListener: WaterAppInnerClickListener
) : ItemViewHolder<WaterAppModelInnerSix>(view) {

    private val binding: FragmentWaterAppInnerSixthBinding = FragmentWaterAppInnerSixthBinding.bind(view)

    override fun attach() {
        super.attach()
        val item = item ?: return
        innerClickListener.onChangePosition(item.id)
    }

    override fun bind(item: WaterAppModelInnerSix) {
        super.bind(item)


    }
}