package com.vodovoz.app.feature.buy_certificate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentBuyCertificateBinding
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BuyCertificateFragment : BaseFragment() {
    override fun layout(): Int {
        return R.layout.fragment_buy_certificate
    }

    private val binding: FragmentBuyCertificateBinding by viewBinding {
        FragmentBuyCertificateBinding.bind(
            contentView
        )
    }

    private val viewModel: BuyCertificateViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUiState()
        viewModel.getBuyCertificateBundle()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUiState().collect { state ->
                    showLoaderWithBg(state.loadingPage)
                    val data = state.data.buyCertificateBundleUI
                    if (data != null) {
                        debugLog { "buy certificate bundle: $data" }
                    }
                }
            }
        }
    }


}