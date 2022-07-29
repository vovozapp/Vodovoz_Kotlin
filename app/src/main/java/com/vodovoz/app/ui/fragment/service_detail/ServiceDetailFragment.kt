package com.vodovoz.app.ui.fragment.service_detail

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.databinding.BsSelectionServiceBinding
import com.vodovoz.app.databinding.FragmentServiceDetailBinding
import com.vodovoz.app.ui.adapter.ServiceNamesAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.fragment.product_filters.ProductFiltersFragment
import com.vodovoz.app.ui.fragment.questionnaires.QuestionnairesViewModel
import com.vodovoz.app.ui.model.FilterUI

private const val SERVICE_TYPE = "SERVICE_TYPE"

class ServiceDetailFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentServiceDetailBinding
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
    ) = FragmentServiceDetailBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initAppBar()
        setupButtons()
        observeViewModel()
        observeResultLiveData()
        binding.scrollableContentContainer.setScrollElevation(binding.appBar)
    }

    private fun initAppBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.let { noNullActionBar ->
            noNullActionBar.setDisplayHomeAsUpEnabled(true)
            noNullActionBar.setDisplayShowHomeEnabled(true)
            noNullActionBar.title = ""
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.serviceName.setOnClickListener {
            findNavController().navigate(ServiceDetailFragmentDirections.actionToServiceSelectionBS(
                viewModel.serviceUIList.toTypedArray(),
                viewModel.selectedServiceType
            ))
        }
    }

    private fun setupButtons() {
        binding.order.setOnClickListener {
            findNavController().navigate(ServiceDetailFragmentDirections.actionToServiceOrderFragment(
                binding.serviceName.text.toString(),
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
            binding.serviceName.text = serviceUI.name
            binding.details.text = HtmlCompat.fromHtml(serviceUI.detail.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

}

class ServiceSelectionBS : BottomSheetDialogFragment() {

    private lateinit var binding: BsSelectionServiceBinding
    private val serviceNamesAdapter = ServiceNamesAdapter { type ->
        findNavController().previousBackStackEntry?.savedStateHandle?.set(SERVICE_TYPE, type)
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BsSelectionServiceBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.servicesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.servicesRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.servicesRecycler.addMarginDecoration { rect, view, parent, state ->

        }
        binding.servicesRecycler.adapter = serviceNamesAdapter

        ServiceSelectionBSArgs.fromBundle(requireArguments()).apply {
            serviceNamesAdapter.serviceDataList = serviceList.map { Pair(it.name, it.type) }
            serviceNamesAdapter.selectedServiceType = selectedService
            serviceNamesAdapter.notifyDataSetChanged()
        }
    }

}