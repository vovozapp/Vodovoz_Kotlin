package com.vodovoz.app.feature.profile.waterapp.viewholder

import android.view.View
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppFourthBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppModelFour
import kotlinx.coroutines.launch

class WaterAppViewHolderFourth(
    view: View,
    clickListener: WaterAppClickListener,
    private val waterAppHelper: WaterAppHelper
) : ItemViewHolder<WaterAppModelFour>(view) {

    private val binding: FragmentWaterAppFourthBinding = FragmentWaterAppFourthBinding.bind(view)

    init {
        binding.cwStart.setOnClickListener {
            val item = item ?: return@setOnClickListener
            waterAppHelper.saveStart(true)
            clickListener.onNextClick(item.id)
        }
    }

    override fun attach() {
        super.attach()
        launch {
            waterAppHelper
                .observeWaterAppRateData()
                .collect {
                    if (it == null) return@collect
                    binding.tvYourRate.text = binding.tvYourRate.context.getString(
                        R.string.millilitres,
                        it.rate
                    )
                }
        }

    }

    override fun bind(item: WaterAppModelFour) {
        super.bind(item)


    }
}