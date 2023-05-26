package com.vodovoz.app.feature.profile.waterapp.viewholder

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppFifthBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelFive
import kotlinx.coroutines.launch

class WaterAppViewHolderFifth(
    view: View,
    clickListener: WaterAppClickListener,
    private val waterAppHelper: WaterAppHelper,
) : ItemViewHolder<WaterAppModelFive>(view) {

    private val binding: FragmentWaterAppFifthBinding = FragmentWaterAppFifthBinding.bind(view)
    private var step: Int = 200
    private var changeStep: Int = 50

    init {
        binding.imgSettings.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onNextClick(item.id)
        }

        binding.imgIncrease.setOnClickListener {
            step += changeStep
            if (step + changeStep > 1000) step = 1000
            binding.tvFillVolume.text = "$step мл"
        }

        binding.imgReduce.setOnClickListener {
            step -= changeStep
            if (step < changeStep) step = changeStep
            binding.tvFillVolume.text = "$step мл"
        }

        binding.imgFill.setOnClickListener {
            fill()
        }

    }

    override fun attach() {
        super.attach()

        launch {
            waterAppHelper
                .observeWaterAppRateData()
                .collect {
                    if (it == null) return@collect

                    binding.tvRate.text = "${it.currentLevel}/${it.rate} мл"


                }
        }

    }

    override fun bind(item: WaterAppModelFive) {
        super.bind(item)

        binding.tvFillVolume.text = "$step мл"

    }

    private fun animView() {
        binding.imgFill.animate().scaleX(1f).scaleY(1f).setDuration(500).withEndAction {
            binding.imgFill.animate().scaleX(1.1f).scaleY(1.1f).setDuration(500).withEndAction { animView() }
        }
    }

    private fun fill() {

    }
}