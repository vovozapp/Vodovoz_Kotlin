package com.vodovoz.app.feature.bottom.services.detail

import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentAboutServicesFlowBinding
import com.vodovoz.app.databinding.FragmentServiceDetailsFlowBinding
import com.vodovoz.app.feature.bottom.services.AboutServicesFlowViewModel
import com.vodovoz.app.ui.fragment.service_detail.ServiceDetailFragmentArgs
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

    private val viewModel: AboutServicesFlowViewModel by activityViewModels()

    private val args: ServiceDetailFragmentArgs by navArgs()

    private val binding: FragmentServiceDetailsFlowBinding by viewBinding {
        FragmentServiceDetailsFlowBinding.bind(
            contentView
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.selectService(args.selectedType)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindErrorRefresh {  }
        observeResultLiveData()
        observeUiState()
        observeEvents()
        initToolbarDropDown("") {
            viewModel.onTitleClick()
        }
        bindButtons()
    }

    private fun bindButtons() {
        binding.btnOrderService.setOnClickListener {
            viewModel.navigateToOrder()
        }
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeAboutServicesEvents()
                .collect {
                    when(it) {
                        is AboutServicesFlowViewModel.AboutServicesEvents.OnTitleClick -> {
                            if (findNavController().currentBackStackEntry?.destination?.id == R.id.serviceOrderFragment) {
                                findNavController().popBackStack()
                            }
                            if (findNavController().currentBackStackEntry?.destination?.id == R.id.serviceSelectionBS) {
                                findNavController().popBackStack()
                            }
                            findNavController().navigate(ServiceDetailFragmentDirections.actionToServiceSelectionBS(
                                emptyArray(), ""
                            ))
                        }
                        is AboutServicesFlowViewModel.AboutServicesEvents.NavigateToOrder -> {
                            if (findNavController().currentBackStackEntry?.destination?.id == R.id.serviceOrderFragment) {
                                findNavController().popBackStack()
                            }
                            if (findNavController().currentBackStackEntry?.destination?.id == R.id.serviceSelectionBS) {
                                findNavController().popBackStack()
                            }
                            findNavController().navigate(ServiceDetailFragmentDirections.actionToServiceOrderFragment(it.name, it.type))
                        }
                        else -> {}
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
                        setToolbarDropDownTitle(state.data.selectedService.name)
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