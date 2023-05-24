package com.vodovoz.app.feature.profile.waterapp

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentWaterAppBinding
import com.vodovoz.app.feature.productdetail.fullscreen.adapter.FullScreenDetailPicturesAdapter
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppAdapter
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppLists
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WaterAppFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_water_app

    private val binding: FragmentWaterAppBinding by viewBinding {
        FragmentWaterAppBinding.bind(contentView)
    }

    private val waterAppAdapter: WaterAppAdapter by lazy {
        WaterAppAdapter(
            waterAppHelper,
            object : WaterAppClickListener {

            }
        )
    }

    @Inject
    lateinit var waterAppHelper: WaterAppHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vpWater.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpWater.isUserInputEnabled = false
        binding.vpWater.adapter = waterAppAdapter

        waterAppAdapter.submitList(WaterAppLists.firstList)

    }


}