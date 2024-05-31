package com.vodovoz.app.feature.bottom.services.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.databinding.FragmentServiceDetailsFlowBinding
import com.vodovoz.app.feature.bottom.services.AboutServicesFlowViewModel
import com.vodovoz.app.util.extensions.prepareServiceHtml
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServiceDetailFragment : BaseFragment() {

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

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindErrorRefresh { }

        binding.webView.settings.javaScriptEnabled = true

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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeAboutServicesEvents()
                    .collect {
                        when (it) {
                            is AboutServicesFlowViewModel.AboutServicesEvents.OnTitleClick -> {
                                if (findNavController().currentBackStackEntry?.destination?.id == R.id.serviceOrderFragment) {
                                    findNavController().popBackStack()
                                }
                                if (findNavController().currentBackStackEntry?.destination?.id == R.id.serviceSelectionBS) {
                                    findNavController().popBackStack()
                                }
                                findNavController().navigate(
                                    ServiceDetailFragmentDirections.actionToServiceSelectionBS(
                                        emptyArray(), ""
                                    )
                                )
                            }

                            is AboutServicesFlowViewModel.AboutServicesEvents.NavigateToOrder -> {
                                if (findNavController().currentBackStackEntry?.destination?.id == R.id.serviceOrderFragment) {
                                    findNavController().popBackStack()
                                }
                                if (findNavController().currentBackStackEntry?.destination?.id == R.id.serviceSelectionBS) {
                                    findNavController().popBackStack()
                                }
                                findNavController().navigate(
                                    ServiceDetailFragmentDirections.actionToServiceOrderFragment(
                                        it.name,
                                        it.type
                                    )
                                )
                            }

                            else -> {}
                        }
                    }
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeUiState()
                    .collect { state ->
                        if (state.loadingPage) {
                            showLoader()
                        } else {
                            hideLoader()
                        }

                        if (state.data.selectedService != null) {
                            setToolbarDropDownTitle(
                                state.data.selectedService.name,
                                state.data.item?.serviceUIList.isNullOrEmpty().not()
                            )
                            binding.webView.loadDataWithBaseURL(ApiConfig.VODOVOZ_URL,
                                state.data.selectedService.detail?.prepareServiceHtml()
                                    ?: "Нет данных",
                                "text/html",
                                "UTF-8",
                                null
                            )
//                            binding.tvDetails.text = state.data.selectedService.detail?.fromHtml()
                            binding.btnOrderService.text = state.data.selectedService.buttonText
                        }



                        showError(state.error)
                    }
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

