package com.vodovoz.app.feature.profile.waterapp.viewholder

import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppFifthBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelFive
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.launch

class WaterAppViewHolderFifth(
    view: View,
    clickListener: WaterAppClickListener,
    private val waterAppHelper: WaterAppHelper,
) : ItemViewHolder<WaterAppModelFive>(view) {

    private val binding: FragmentWaterAppFifthBinding = FragmentWaterAppFifthBinding.bind(view)
    private var step: Int = 200
    private var changeStep: Int = 50
    private var bottleHeight: Int = 0
    private var canFill: Boolean = true

    init {
        binding.imgSettings.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onNextClick(item.id)
        }

        binding.imgIncrease.setOnClickListener {
            step += changeStep
            if (step + changeStep > 1000) step = 1000
            binding.tvFillVolume.text = binding.tvFillVolume.context.getString(
                    R.string.millilitres,
                    step
            )
        }

        binding.imgReduce.setOnClickListener {
            step -= changeStep
            if (step < changeStep) step = changeStep
            binding.tvFillVolume.text = binding.tvFillVolume.context.getString(
                R.string.millilitres,
                step
            )
        }

        binding.imgFill.setOnClickListener {
            waterAppHelper.tryToChangeWaterLevel(step)
        }

    }

    override fun attach() {
        super.attach()

        launch {
            waterAppHelper
                .observeWaterAppRateData()
                .collect {
                    if (it == null) return@collect
                    canFill = it.canFill
                    if (!canFill) {
                        Toast.makeText(itemView.context, "Вы уже выпили вашу норму", Toast.LENGTH_SHORT).show()
                    }
                    fill(it.currentLevel, it.rate)

                }
        }

        animView()

    }

    override fun detach() {
        super.detach()
        debugLog { "save water rate" }
        waterAppHelper.saveWaterAppRateData()
    }

    override fun bind(item: WaterAppModelFive) {
        super.bind(item)

        binding.tvFillVolume.text = binding.tvFillVolume.context.getString(
            R.string.millilitres,
            step
        )

    }

    private fun animView() {
        binding.imgFill.animate().scaleX(1f).scaleY(1f).setDuration(500).withEndAction {
            binding.imgFill.animate().scaleX(1.1f).scaleY(1.1f).setDuration(500).withEndAction { animView() }
        }
    }

    private fun fill(currentLevel: Int, rate: Int) {
        binding.imgWaterBottle.post {
            bottleHeight = binding.imgWaterBottle.height
            val percent: Double = currentLevel.toDouble() / rate.toDouble()
            val height: Int = (percent * bottleHeight.toDouble()).toInt()
            val lavParams = binding.lavAnimation.layoutParams as ConstraintLayout.LayoutParams
            lavParams.height = height
            binding.lavAnimation.layoutParams = lavParams
            val vParams = binding.vWaterBackground.layoutParams as ConstraintLayout.LayoutParams
            if (height > bottleHeight) {
                vParams.height = bottleHeight
            } else {
                vParams.height = height - 3
            }
            binding.vWaterBackground.layoutParams = vParams
            binding.tvRate.text = buildString {
                            append(currentLevel)
                            append("/")
                            append(rate)
                            append(" мл")
                        }
        }
    }
}