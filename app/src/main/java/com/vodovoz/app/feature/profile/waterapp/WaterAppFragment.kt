package com.vodovoz.app.feature.profile.waterapp

import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentWaterAppBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WaterAppFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_water_app

    private val binding: FragmentWaterAppBinding by viewBinding {
        FragmentWaterAppBinding.bind(contentView)
    }


}