package com.vodovoz.app.feature.bottom.services.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentServiceDetailsFlowBinding
import com.vodovoz.app.feature.bottom.services.AboutServicesFlowViewModel
import com.vodovoz.app.util.extensions.fromHtml
import dagger.hilt.android.AndroidEntryPoint

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
                        setToolbarDropDownTitle(state.data.selectedService.name, state.data.item?.serviceUIList.isNullOrEmpty().not())
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

/*@AndroidEntryPoint
class ServiceDetailFragment : ViewStateBaseFragment() {

    companion object {
        const val SERVICE_TYPE = "SERVICE_TYPE"
    }

    private lateinit var binding: FragmentServiceDetailsBinding
    private val viewModel: ServiceDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    override fun update() {
        viewModel.fetchServices()
    }

    private fun getArgs() {
        ServiceDetailFragmentArgs.fromBundle(requireArguments()).apply {
            viewModel.updateArgs(
                serviceTypeList = typeList.toList(),
                selectedServiceType = selectedType
            )
        }
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentServiceDetailsBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initAppBar()
        setupButtons()
        observeViewModel()
        observeResultLiveData()
        binding.nsvContent.setScrollElevation(binding.incAppBar.apAppBar)
    }

    private fun initAppBar() {
        binding.incAppBar.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.incAppBar.tvDropDownTitle.setOnClickListener {
            findNavController().navigate(ServiceDetailFragmentDirections.actionToServiceSelectionBS(
                viewModel.serviceUIList.toTypedArray(),
                viewModel.selectedServiceType
            ))
        }
    }

    private fun setupButtons() {
        binding.btnOrderService.setOnClickListener {
            findNavController().navigate(ServiceDetailFragmentDirections.actionToServiceOrderFragment(
                binding.incAppBar.tvDropDownTitle.text.toString(),
                viewModel.selectedServiceType
            ))
        }
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(SERVICE_TYPE)?.observe(viewLifecycleOwner) { type ->
                viewModel.selectService(type)
            }
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { viewState ->
            when(viewState) {
                is ViewState.Success -> onStateSuccess()
                is ViewState.Loading -> onStateLoading()
                is ViewState.Hide -> onStateHide()
                is ViewState.Error -> onStateError(viewState.errorMessage)
            }
        }

        viewModel.serviceUILD.observe(viewLifecycleOwner) { serviceUI ->
            binding.incAppBar.tvDropDownTitle.text = serviceUI.name
            binding.tvDetails.text = HtmlCompat.fromHtml(serviceUI.detail.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

}*/

