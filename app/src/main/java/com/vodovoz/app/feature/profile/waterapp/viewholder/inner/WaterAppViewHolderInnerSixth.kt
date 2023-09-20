package com.vodovoz.app.feature.profile.waterapp.viewholder.inner

import android.view.View
import androidx.core.content.ContextCompat
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppInnerSixthBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.model.inner.WaterAppModelInnerSix
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.launch

class WaterAppViewHolderInnerSixth(
    view: View,
    private val waterAppHelper: WaterAppHelper,
) : ItemViewHolder<WaterAppModelInnerSix>(view) {

    private val binding: FragmentWaterAppInnerSixthBinding =
        FragmentWaterAppInnerSixthBinding.bind(view)

    init {
        binding.llLowContainer.setOnClickListener { waterAppHelper.saveSport("0.25") }
        binding.llMediumContainer.setOnClickListener { waterAppHelper.saveSport("0.375") }
        binding.llHighContainer.setOnClickListener { waterAppHelper.saveSport("0.5") }
    }

    override fun attach() {
        super.attach()

        launch {
            waterAppHelper
                .observeWaterAppUserData()
                .collect {
                    if (it == null || it.sport.isEmpty()) return@collect
                    debugLog { "inner sport ${it.sport} ${it.sport.toFloat()}" }

                    when (it.sport) {
                        "0.25" -> selectLow()
                        "0.375" -> selectMedium()
                        "0.5" -> selectHigh()
                    }
                }
        }
    }

    private fun selectLow() {
        binding.cwLowContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.bluePrimary
            )
        )
        binding.cwMediumContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.gray_unselected
            )
        )
        binding.cwHighContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.gray_unselected
            )
        )

    }

    private fun selectMedium() {
        binding.cwLowContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.gray_unselected
            )
        )
        binding.cwMediumContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.bluePrimary
            )
        )
        binding.cwHighContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.gray_unselected
            )
        )

    }

    private fun selectHigh() {

        binding.cwLowContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.gray_unselected
            )
        )
        binding.cwMediumContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.gray_unselected
            )
        )
        binding.cwHighContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.bluePrimary
            )
        )

    }
}