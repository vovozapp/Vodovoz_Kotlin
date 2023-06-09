package com.vodovoz.app.feature.addresses

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentAddressesFlowBinding
import com.vodovoz.app.feature.addresses.adapter.AddressesClickListener
import com.vodovoz.app.feature.map.MapController
import com.vodovoz.app.feature.map.MapFlowViewModel
import com.vodovoz.app.feature.map.adapter.AddressResultClickListener
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.snack
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.user_location.UserLocationLayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddressesFragment : BaseFragment() {

    companion object {
        const val SELECTED_ADDRESS = "SELECTED_ADDRESS"
    }

    override fun layout(): Int = R.layout.fragment_addresses_flow

    private val binding: FragmentAddressesFlowBinding by viewBinding {
        FragmentAddressesFlowBinding.bind(
            contentView
        )
    }

    @Inject
    lateinit var tabManager: TabManager

    private val viewModel: AddressesFlowViewModel by viewModels()
    private val mapViewModel: MapFlowViewModel by viewModels()

    private val userLocationLayer: UserLocationLayer by lazy {
        mapKit.createUserLocationLayer(binding.mapView.mapWindow)
    }
    private val mapKit: MapKit by lazy {
        MapKitFactory.getInstance()
    }

    private val mapController by lazy {
        MapController(
            mapKit,
            object: AddressResultClickListener {},
            userLocationLayer,
            mapViewModel,
            requireContext(),
            requireActivity()
        ) {}
    }

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

        mapController.initMap(binding.mapView)
        mapController.initSearch()

        addressesController.bind(binding.rvAddresses, binding.refreshContainer)
        initToolbar(resources.getString(R.string.addresses_title))
        bindErrorRefresh { viewModel.refresh() }
        initAddAddressButton()
        observeUiState()
        observeEvents()
        observeRefresh()
    }

    override fun onStart() {
        super.onStart()
        mapKit.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        mapKit.onStop()
        super.onStop()
    }

    private fun observeRefresh() {
        lifecycleScope.launchWhenStarted {
            tabManager
                .observeAddressesRefresh()
                .collect {
                    if (it) {
                        viewModel.refresh()
                        tabManager.setAddressesRefreshState(false)
                    }
                }
        }
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
                                SELECTED_ADDRESS, it.address)
                            findNavController().popBackStack(R.id.orderingFragment, false)
                        }
                        is AddressesFlowViewModel.AddressesEvents.UpdateAddress -> {
                            mapController.searchForUpdate(it.address)
                        }
                    }
                }
        }

        lifecycleScope.launchWhenStarted {
            mapViewModel.observeEvent()
                .collect {
                    when(it) {
                        is MapFlowViewModel.MapFlowEvents.Submit -> {
                            it.list.forEach { point ->
                                mapController.submitRequest(point, it.startPoint)
                            }
                        }
                        is MapFlowViewModel.MapFlowEvents.UpdatePendingAddressUISuccess -> {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                SELECTED_ADDRESS, it.address)
                            findNavController().popBackStack(R.id.orderingFragment, false)
                        }
                        else -> {}
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

enum class AddressType {
    Personal, Company
}

enum class OpenMode {
    SelectAddress
}