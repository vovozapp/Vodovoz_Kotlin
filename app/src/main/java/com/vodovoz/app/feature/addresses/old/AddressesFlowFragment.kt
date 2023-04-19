package com.vodovoz.app.feature.addresses.old

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentAddressesFlowBinding
import com.vodovoz.app.feature.addresses.AddressesController
import com.vodovoz.app.feature.addresses.AddressesFlowViewModel
import com.vodovoz.app.feature.addresses.AddressesFragment
import com.vodovoz.app.feature.addresses.AddressesFragmentDirections
import com.vodovoz.app.feature.addresses.adapter.AddressesClickListener
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressesFlowFragment : BaseFragment() {

    companion object {
        const val SELECTED_ADDRESS = "SELECTED_ADDRESS"
    }

    override fun layout(): Int = R.layout.fragment_addresses_flow

    private val binding: FragmentAddressesFlowBinding by viewBinding {
        FragmentAddressesFlowBinding.bind(
            contentView
        )
    }

    private val viewModel: AddressesFlowViewModel by viewModels()

    private val addressesController by lazy {
        AddressesController(
            viewModel = viewModel,
            listener = getAddressesClickListener(),
            context = requireContext()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addressesController.bind(binding.rvAddresses, binding.refreshContainer)
        initToolbar(resources.getString(R.string.addresses_title))
        bindErrorRefresh { viewModel.refresh() }
        initAddAddressButton()
        observeUiState()
        observeEvents()

    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { state ->
                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    if (state.data.fullList.isNotEmpty()) {
                        addressesController.submitList(state.data.fullList)
                    }

                    showError(state.error)
                }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeEvent()
                .collect {
                    when (it) {
                        is AddressesFlowViewModel.AddressesEvents.DeleteEvent -> {
                            requireActivity().snack(it.message)
                        }
                        is AddressesFlowViewModel.AddressesEvents.OnAddressClick -> {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                AddressesFragment.SELECTED_ADDRESS, it.address)
                            findNavController().popBackStack()
                        }
                    }
                }
        }
    }

    private fun getAddressesClickListener(): AddressesClickListener {
        return object : AddressesClickListener {
            override fun onAddressClick(item: AddressUI) {
                viewModel.onAddressClick(item)
            }

            override fun onEditClick(item: AddressUI) {
                findNavController().navigate(
                    AddressesFragmentDirections.actionToMapDialogFragment(
                        item
                    )
                )
            }

            override fun onDelete(item: AddressUI) {
                showDeleteAddressDialog(item.id)
            }
        }
    }

    private fun initAddAddressButton() {
        binding.btnAddAddress.setOnClickListener {
            findNavController().navigate(AddressesFragmentDirections.actionToMapDialogFragment())
        }
    }

    private fun showDeleteAddressDialog(addressId: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Удалить адрес?")
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.confirm)) { dialog, which ->
                viewModel.deleteAddress(addressId)
                dialog.cancel()
            }
            .show()
    }


}