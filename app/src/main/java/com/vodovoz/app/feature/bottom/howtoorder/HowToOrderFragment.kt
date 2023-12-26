package com.vodovoz.app.feature.bottom.howtoorder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentHowToOrderFlowBinding
import com.vodovoz.app.feature.bottom.howtoorder.adapter.HowToOrderFlowAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HowToOrderFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_how_to_order_flow

    private val binding: FragmentHowToOrderFlowBinding by viewBinding {
        FragmentHowToOrderFlowBinding.bind(
            contentView
        )
    }
    private val viewModel: HowToOrderFlowViewModel by viewModels()

    private val howToOrderAdapter = HowToOrderFlowAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(resources.getString(R.string.how_order_title))
        observeList()
    }

    private fun observeList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeHowToOrderSteps()
                    .collect {
                        binding.vpHowOrder.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                        howToOrderAdapter.submitList(it)
                        binding.vpHowOrder.adapter = howToOrderAdapter
                        TabLayoutMediator(
                            binding.tlIndicators,
                            binding.vpHowOrder
                        ) { _, _ -> }.attach()
                    }
            }
        }
    }

}
