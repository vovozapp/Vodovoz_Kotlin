package com.vodovoz.app.feature.blockapp

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentBlockAppBinding
import com.vodovoz.app.feature.sitestate.SiteStateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class BlockAppFragment : BaseFragment() {

    override fun layout(): Int {
        return R.layout.fragment_block_app
    }

    private val binding: FragmentBlockAppBinding by viewBinding {
        FragmentBlockAppBinding.bind(contentView)
    }

    @Inject
    lateinit var siteStateManager: SiteStateManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSiteState()
    }

    private fun observeSiteState() {
        lifecycleScope.launchWhenStarted {
            siteStateManager
                .observeSiteState()
                .collect {
                    if (it != null) {

                    }
                }
        }
    }

}