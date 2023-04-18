package com.vodovoz.app.ui.fragment.service_detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsSelectionServicesBinding
import com.vodovoz.app.ui.adapter.ServiceNamesAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.fragment.service_detail.ServiceDetailFragment.Companion.SERVICE_TYPE

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