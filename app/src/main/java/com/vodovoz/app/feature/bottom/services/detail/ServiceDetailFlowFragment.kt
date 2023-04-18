package com.vodovoz.app.feature.bottom.services.detail

import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentAboutServicesFlowBinding
import com.vodovoz.app.databinding.FragmentServiceDetailsFlowBinding
import com.vodovoz.app.feature.bottom.services.AboutServicesFlowViewModel
import com.vodovoz.app.ui.fragment.service_detail.ServiceDetailFragmentDirections
import com.vodovoz.app.util.extensions.fromHtml
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ServiceDetailFlowFragment : BaseFragment() {

    companion object {
        const val SERVICE_TYPE = "SERVICE_TYPE"
    }

    override fun layout(): Int = R.layout.fragment_service_details_flow

    private val viewModel: ServiceDetailFlowViewModel by viewModels()

    private val binding: FragmentServiceDetailsFlowBinding by viewBinding {
        FragmentServiceDetailsFlowBinding.bind(
            contentView
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindErrorRefresh {  }
        observeResultLiveData()
        observeUiState()
        observeEvents()
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeEvent()
                .collect {
                    when(it) {
                        is ServiceDetailFlowViewModel.ServiceDetailEvents.OnTitleClick -> {

                        }
                    }
                }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeUiState()
                .collect { state ->
                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    if (state.data.selectedService != null) {
                        initToolbarDropDown(state.data.selectedService.name) {
                            viewModel.onTitleClick()
                        }
                        binding.tvDetails.text = state.data.selectedService.detail.fromHtml()
                    }

                    showError(state.error)
                }
        }
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(SERVICE_TYPE)?.observe(viewLifecycleOwner) { type ->
                viewModel.selectService(type)
            }
    }

}