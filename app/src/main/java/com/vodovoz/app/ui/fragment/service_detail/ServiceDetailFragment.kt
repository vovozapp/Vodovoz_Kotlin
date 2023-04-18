package com.vodovoz.app.ui.fragment.service_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.databinding.FragmentServiceDetailsBinding
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

}

