package com.vodovoz.app.feature.profile.waterapp.viewholder

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppFirstBinding
import com.vodovoz.app.databinding.FragmentWaterAppThirdBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppAdapter
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppLists
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelOne
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelThree

class WaterAppViewHolderThird(
    view: View,
    clickListener: WaterAppClickListener,
    waterAppHelper: WaterAppHelper
) : ItemViewHolder<WaterAppModelThree>(view) {

    private val binding: FragmentWaterAppThirdBinding = FragmentWaterAppThirdBinding.bind(view)

    private val waterAppAdapter: WaterAppAdapter = WaterAppAdapter(waterAppHelper, clickListener)

    init {
        binding.tvNext.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onNextClick(item.id)
        }

        binding.imgBack.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onPrevClick(item.id)
        }

        binding.vpSteps.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpSteps.adapter = waterAppAdapter
    }

    override fun bind(item: WaterAppModelThree) {
        super.bind(item)

        waterAppAdapter.submitList(WaterAppLists.innerList)
    }
}