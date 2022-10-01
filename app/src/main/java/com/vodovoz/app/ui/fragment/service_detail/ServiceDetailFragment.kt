package com.vodovoz.app.ui.fragment.service_detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsSelectionServicesBinding
import com.vodovoz.app.databinding.FragmentServiceDetailsBinding
import com.vodovoz.app.ui.adapter.ServiceNamesAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation

private const val SERVICE_TYPE = "SERVICE_TYPE"

class ServiceDetailFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentServiceDetailsBinding
    private lateinit var viewModel: ServiceDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
    }

    override fun update() {
        viewModel.fetchServices()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[ServiceDetailViewModel::class.java]
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

class ServiceSelectionBS : BottomSheetDialogFragment() {

    private lateinit var binding: BsSelectionServicesBinding
    private val serviceNamesAdapter = ServiceNamesAdapter { type ->
        findNavController().previousBackStackEntry?.savedStateHandle?.set(SERVICE_TYPE, type)
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BsSelectionServicesBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupServicesRecycler()
    }

    private fun setupServicesRecycler() {
        binding.rvServices.layoutManager = LinearLayoutManager(requireContext())
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.rvServices.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.top = space
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom = space
        }
        binding.rvServices.adapter = serviceNamesAdapter

        ServiceSelectionBSArgs.fromBundle(requireArguments()).apply {
            serviceNamesAdapter.serviceDataList = serviceList.map { Pair(it.name, it.type) }
            serviceNamesAdapter.selectedServiceType = selectedService
            serviceNamesAdapter.notifyDataSetChanged()
        }
    }

}