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
                         //   mapController.searchForUpdate(it.address)
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

/*@AndroidEntryPoint
class AddressesFragment : ViewStateBaseFragment() {

    companion object {
        const val SELECTED_ADDRESS = "SELECTED_ADDRESS"
    }

    private lateinit var binding: FragmentAddressesBinding
    private val viewModel: SavedAddressesViewModel by viewModels()

    private val addressesAdapter = AddressesAdapter()
    private var dividerItemDecoration: Divider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updateData()
        getArgs()
    }

    private fun getArgs() {
        AddressesFragmentArgs.fromBundle(requireArguments()).let { args ->
            if (args.openMode != "null") {
                if (args.addressType != "null") {
                    args.addressType?.let { addressType ->
                        viewModel.updateArgs(OpenMode.valueOf(args.openMode.toString()), OrderType.valueOf(args.addressType.toString()))
                    }
                }
            }
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

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentAddressesBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun update() { viewModel.updateData() }

    override fun initView() {
        initActionBar()
        initAddAddressButton()
        initAddressesRecyclers()
        observeViewModel()
    }

    private fun initActionBar() {
        binding.incAppBar.tvTitle.text = resources.getString(R.string.addresses_title)
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initAddAddressButton() {
        binding.btnAddAddress.setOnClickListener {
            findNavController().navigate(AddressesFragmentDirections.actionToMapDialogFragment())
        }
    }

    private fun initAddressesRecyclers() {
        val space16 = resources.getDimension(R.dimen.space_16).toInt()
        val spaceLastItemBottomSpace = resources.getDimension(R.dimen.last_item_bottom_normal_space).toInt()
        binding.rvAddresses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddresses.adapter = addressesAdapter
        binding.rvAddresses.addMarginDecoration { rect, view, parent, state ->
            rect.top = space16
            val position = parent.getChildAdapterPosition(view)
            if (parent.adapter?.getItemViewType(position) == AddressesAdapterItemType.ADDRESS.value) {
                if (position == state.itemCount - 1) rect.bottom = spaceLastItemBottomSpace
            }
        }

        addressesAdapter.setupListeners(
            deleteAddress = { addressUI ->
                showDeleteAddressDialog(addressUI.id)
            },
            editAddress = { addressUI ->
                findNavController().navigate(AddressesFragmentDirections.actionToMapDialogFragment(addressUI))
            },
            onAddressClick = { addressUI ->
                if (viewModel.openMode == OpenMode.SelectAddress) {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(SELECTED_ADDRESS, addressUI)
                    findNavController().popBackStack()
                }
            }
        )
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Success -> onStateSuccess()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Hide -> onStateHide()
                is ViewState.Loading -> onStateLoading()
            }
        }

        viewModel.addressUIListLD.observe(viewLifecycleOwner) { pair ->
            updateAddresses(pair)
        }

        viewModel.errorLD.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun updateAddresses(pair: Pair<List<AddressUI>, List<AddressUI>>) {
        val itemList = mutableListOf<AddressesAdapterItem>()
        if (pair.first.isNotEmpty() && viewModel.orderType != OrderType.COMPANY) {
            itemList.add(AddressesAdapterItem.AddressesTypeTitle(resources.getString(R.string.personal_addresses_title)))
            itemList.addAll(pair.first.map { AddressesAdapterItem.Address(it) })
        }
        if (pair.second.isNotEmpty() && viewModel.orderType != OrderType.PERSONAL) {
            itemList.add(AddressesAdapterItem.AddressesTypeTitle(resources.getString(R.string.company_addresses_title)))
            itemList.addAll(pair.second.map { AddressesAdapterItem.Address(it) })
        }
        addressesAdapter.updateData(itemList)
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateData()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}*/

enum class AddressType {
    Personal, Company
}

enum class OpenMode {
    SelectAddress
}