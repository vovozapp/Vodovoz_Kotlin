package com.vodovoz.app.feature.profile.waterapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentWaterAppBinding
import com.vodovoz.app.feature.productdetail.fullscreen.adapter.FullScreenDetailPicturesAdapter
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppAdapter
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppInnerClickListener
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppLists
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WaterAppFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_water_app

    private val binding: FragmentWaterAppBinding by viewBinding {
        FragmentWaterAppBinding.bind(contentView)
    }

    private val viewModel: WaterAppViewModel by viewModels()

    private val waterAppAdapter: WaterAppAdapter by lazy {
        WaterAppAdapter(
            waterAppHelper,
            object : WaterAppClickListener {
                override fun onNextClick(currentPosition: Int) {
                    binding.vpWater.currentItem = currentPosition
                }

                override fun onNextClick(currentPosition: Int, saveData: Boolean) {
                    binding.vpWater.currentItem = currentPosition
                    viewModel.saveWaterAppUserData()
                }

                override fun onPrevClick(currentPosition: Int) {
                    binding.vpWater.currentItem = currentPosition - 2
                }

                override fun saveGender(gender: String) {
                    viewModel.saveGender(gender)
                }

                override fun saveHeight(height: String) {
                    viewModel.saveHeight(height)
                }

                override fun saveWeight(weight: String) {
                    viewModel.saveWeight(weight)
                }

                override fun saveSleepTime(sleepTime: String) {
                    viewModel.saveSleepTime(sleepTime)
                }

                override fun saveWakeUpTime(wakeUpTime: String) {
                    viewModel.saveWakeUpTime(wakeUpTime)
                }

                override fun saveSport(sport: String) {
                    viewModel.saveSport(sport)
                }

            },
            object : WaterAppInnerClickListener {
                override fun onChangePosition(position: Int) {

                }
            }
        )
    }
    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var waterAppHelper: WaterAppHelper

    override fun onStart() {
        super.onStart()
        tabManager.changeTabVisibility(false)
    }

    override fun onStop() {
        super.onStop()
        tabManager.changeTabVisibility(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vpWater.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpWater.isUserInputEnabled = false
        binding.vpWater.adapter = waterAppAdapter

        waterAppAdapter.submitList(WaterAppLists.firstList)

    }


}