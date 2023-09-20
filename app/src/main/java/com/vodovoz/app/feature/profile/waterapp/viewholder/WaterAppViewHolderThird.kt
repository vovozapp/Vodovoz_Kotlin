package com.vodovoz.app.feature.profile.waterapp.viewholder

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppThirdBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppAdapter
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppLists
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelThree

class WaterAppViewHolderThird(
    view: View,
    clickListener: WaterAppClickListener,
    waterAppHelper: WaterAppHelper,
) : ItemViewHolder<WaterAppModelThree>(view) {

    private val binding: FragmentWaterAppThirdBinding = FragmentWaterAppThirdBinding.bind(view)

    private val waterAppAdapter: WaterAppAdapter =
        WaterAppAdapter(waterAppHelper, clickListener)

    private val onPageChangeCallback = object : OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            mapIconsByPosition(position + 1)
        }
    }

    init {
        with(binding) {
            tvNext.setOnClickListener {
                val item = item ?: return@setOnClickListener
                waterAppHelper.saveRate()
                clickListener.onNextClick(item.id, true)
            }

            imgBack.setOnClickListener {
                val item = item ?: return@setOnClickListener
                clickListener.onPrevClick(item.id)
            }

            vpSteps.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            vpSteps.adapter = waterAppAdapter
            vpSteps.registerOnPageChangeCallback(onPageChangeCallback)

            ivArrowLeft.setOnClickListener {
                vpSteps.setCurrentItem(vpSteps.currentItem - 1, true)
            }
            ivArrowRight.setOnClickListener {
                vpSteps.setCurrentItem(vpSteps.currentItem + 1, true)
            }

            mapIconsByPosition(1)
        }
    }

    override fun bind(item: WaterAppModelThree) {
        super.bind(item)

        waterAppAdapter.submitList(WaterAppLists.innerList)
        binding.tvNext.isEnabled = false
        binding.tvNext.isSelected = false
    }

    internal fun mapIconsByPosition(position: Int) {
        if (position == 1) {
            binding.imgStepGender.isSelected = true
            binding.imgStepHeight.isSelected = false
            binding.imgStepWeight.isSelected = false
            binding.imgStepWakeUp.isSelected = false
            binding.imgStepSleepTime.isSelected = false
            binding.imgStepSport.isSelected = false
            binding.tvTitle.text = "Ваш пол"
            binding.ivArrowLeft.visibility = View.INVISIBLE
            binding.ivArrowRight.visibility = View.VISIBLE
        }

        if (position == 2) {
            binding.imgStepGender.isSelected = true
            binding.imgStepHeight.isSelected = true
            binding.imgStepWeight.isSelected = false
            binding.imgStepWakeUp.isSelected = false
            binding.imgStepSleepTime.isSelected = false
            binding.imgStepSport.isSelected = false
            binding.tvTitle.text = "Ваш рост"
            binding.ivArrowLeft.visibility = View.VISIBLE
            binding.ivArrowRight.visibility = View.VISIBLE
        }

        if (position == 3) {
            binding.imgStepGender.isSelected = true
            binding.imgStepHeight.isSelected = true
            binding.imgStepWeight.isSelected = true
            binding.imgStepWakeUp.isSelected = false
            binding.imgStepSleepTime.isSelected = false
            binding.imgStepSport.isSelected = false
            binding.tvTitle.text = "Ваш вес"
            binding.ivArrowLeft.visibility = View.VISIBLE
            binding.ivArrowRight.visibility = View.VISIBLE
        }

        if (position == 4) {
            binding.imgStepGender.isSelected = true
            binding.imgStepHeight.isSelected = true
            binding.imgStepWeight.isSelected = true
            binding.imgStepWakeUp.isSelected = false
            binding.imgStepSleepTime.isSelected = true
            binding.imgStepSport.isSelected = false
            binding.tvTitle.text = "Время для сна"
            binding.ivArrowLeft.visibility = View.VISIBLE
            binding.ivArrowRight.visibility = View.VISIBLE
        }

        if (position == 5) {
            binding.imgStepGender.isSelected = true
            binding.imgStepHeight.isSelected = true
            binding.imgStepWeight.isSelected = true
            binding.imgStepWakeUp.isSelected = true
            binding.imgStepSleepTime.isSelected = true
            binding.imgStepSport.isSelected = false
            binding.tvTitle.text = "Время пробуждения"
            binding.ivArrowLeft.visibility = View.VISIBLE
            binding.ivArrowRight.visibility = View.VISIBLE
        }

        if (position == 6) {
            binding.imgStepGender.isSelected = true
            binding.imgStepHeight.isSelected = true
            binding.imgStepWeight.isSelected = true
            binding.imgStepWakeUp.isSelected = true
            binding.imgStepSleepTime.isSelected = true
            binding.imgStepSport.isSelected = true
            binding.tvNext.isEnabled = true
            binding.tvNext.isSelected = true
            binding.tvTitle.text = "Уровень активности"
            binding.ivArrowLeft.visibility = View.VISIBLE
            binding.ivArrowRight.visibility = View.INVISIBLE
        }
    }

    override fun detach() {
        super.detach()
        binding.vpSteps.unregisterOnPageChangeCallback(onPageChangeCallback)
    }

}