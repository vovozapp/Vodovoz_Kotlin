package com.vodovoz.app.feature.profile.waterapp.viewholder

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppFirstBinding
import com.vodovoz.app.databinding.FragmentWaterAppThirdBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppAdapter
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppInnerClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppLists
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelOne
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelThree

class WaterAppViewHolderThird(
    view: View,
    clickListener: WaterAppClickListener,
    waterAppHelper: WaterAppHelper
) : ItemViewHolder<WaterAppModelThree>(view) {

    private val binding: FragmentWaterAppThirdBinding = FragmentWaterAppThirdBinding.bind(view)

    private val waterAppAdapter: WaterAppAdapter = WaterAppAdapter(waterAppHelper, clickListener, fetchInnerClickListener())

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

        mapIconsByPosition(1)
    }

    override fun bind(item: WaterAppModelThree) {
        super.bind(item)

        waterAppAdapter.submitList(WaterAppLists.innerList)
    }

    internal fun mapIconsByPosition(position: Int) {
        if (position == 1) {
            binding.imgStepGender.isSelected = true
            binding.imgStepHeight.isSelected = false
            binding.imgStepWeight.isSelected = false
            binding.imgStepWakeUp.isSelected = false
            binding.imgStepSleepTime.isSelected = false
            binding.imgStepSport.isSelected = false
        }

        if (position == 2) {
            binding.imgStepGender.isSelected = false
            binding.imgStepHeight.isSelected = true
            binding.imgStepWeight.isSelected = false
            binding.imgStepWakeUp.isSelected = false
            binding.imgStepSleepTime.isSelected = false
            binding.imgStepSport.isSelected = false
        }

        if (position == 3) {
            binding.imgStepGender.isSelected = false
            binding.imgStepHeight.isSelected = false
            binding.imgStepWeight.isSelected = true
            binding.imgStepWakeUp.isSelected = false
            binding.imgStepSleepTime.isSelected = false
            binding.imgStepSport.isSelected = false
        }

        if (position == 4) {
            binding.imgStepGender.isSelected = false
            binding.imgStepHeight.isSelected = false
            binding.imgStepWeight.isSelected = false
            binding.imgStepWakeUp.isSelected = false
            binding.imgStepSleepTime.isSelected = true
            binding.imgStepSport.isSelected = false
        }

        if (position == 5) {
            binding.imgStepGender.isSelected = false
            binding.imgStepHeight.isSelected = false
            binding.imgStepWeight.isSelected = false
            binding.imgStepWakeUp.isSelected = true
            binding.imgStepSleepTime.isSelected = false
            binding.imgStepSport.isSelected = false
        }

        if (position == 6) {
            binding.imgStepGender.isSelected = false
            binding.imgStepHeight.isSelected = false
            binding.imgStepWeight.isSelected = false
            binding.imgStepWakeUp.isSelected = false
            binding.imgStepSleepTime.isSelected = false
            binding.imgStepSport.isSelected = true
        }
    }

    private fun fetchInnerClickListener() : WaterAppInnerClickListener {
        return object : WaterAppInnerClickListener {
            override fun onChangePosition(position: Int) {
                mapIconsByPosition(position)
            }
        }
    }
}