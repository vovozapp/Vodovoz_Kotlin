package com.vodovoz.app.feature.profile.waterapp.viewholder.inner

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppFirstBinding
import com.vodovoz.app.databinding.FragmentWaterAppInnerFirstBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppInnerClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelOne
import com.vodovoz.app.feature.profile.waterapp.model.inner.WaterAppModelInnerOne

class WaterAppViewHolderInnerFirst(
    view: View,
    clickListener: WaterAppClickListener,
    waterAppHelper: WaterAppHelper,
    private val innerClickListener: WaterAppInnerClickListener
) : ItemViewHolder<WaterAppModelInnerOne>(view) {

    private val binding: FragmentWaterAppInnerFirstBinding = FragmentWaterAppInnerFirstBinding.bind(view)

    init {

    }

    override fun attach() {
        super.attach()
        val item = item ?: return
        innerClickListener.onChangePosition(item.id)
    }

    override fun bind(item: WaterAppModelInnerOne) {
        super.bind(item)


    }
}