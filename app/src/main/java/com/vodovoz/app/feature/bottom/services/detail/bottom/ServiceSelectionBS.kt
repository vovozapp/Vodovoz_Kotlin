package com.vodovoz.app.feature.bottom.services.detail.bottom

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsSelectionServicesBinding
import com.vodovoz.app.feature.bottom.services.AboutServicesFlowViewModel
import com.vodovoz.app.feature.bottom.services.detail.ServiceDetailFragment
import com.vodovoz.app.feature.bottom.services.detail.bottom.adapter.ServiceNamesFlowAdapter
import com.vodovoz.app.feature.bottom.services.detail.bottom.adapter.ServiceNamesFlowClickListener
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServiceSelectionBS : BaseBottomSheetFragment() {

    override fun layout(): Int = R.layout.bs_selection_services

    private val binding: BsSelectionServicesBinding by viewBinding { BsSelectionServicesBinding.bind(contentView) }

    private val viewModel: AboutServicesFlowViewModel by activityViewModels()

    private val args: ServiceSelectionBSArgs by navArgs()

    private val serviceNamesAdapter = ServiceNamesFlowAdapter(object : ServiceNamesFlowClickListener {
        override fun onServiceClick(type: String) {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                ServiceDetailFragment.SERVICE_TYPE, type)
            dismiss()
        }
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initList()
        observeUiState()
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeUiState()
                .collect {
                    if (it.data.nameItemList.isNotEmpty()) {
                        serviceNamesAdapter.submitList(it.data.nameItemList)
                    }
                }
        }
    }

    private fun initList() {
        binding.rvServices.layoutManager = LinearLayoutManager(requireContext())
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.rvServices.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.top = space
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom = space
        }
        binding.rvServices.adapter = serviceNamesAdapter
    }
}
/*
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

}*/
