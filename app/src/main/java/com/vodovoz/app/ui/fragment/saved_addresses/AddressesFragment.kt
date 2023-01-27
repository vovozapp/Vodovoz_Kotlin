package com.vodovoz.app.ui.fragment.saved_addresses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentAddressesBinding
import com.vodovoz.app.ui.adapter.AddressesAdapter
import com.vodovoz.app.ui.adapter.AddressesAdapterItem
import com.vodovoz.app.ui.adapter.AddressesAdapterItemType
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.fragment.ordering.OrderType
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.ui.view.Divider

class AddressesFragment : ViewStateBaseFragment() {

    companion object {
        const val SELECTED_ADDRESS = "SELECTED_ADDRESS"
    }

    private lateinit var binding: FragmentAddressesBinding
    private lateinit var viewModel: SavedAddressesViewModel

    private val addressesAdapter = AddressesAdapter()
    private var dividerItemDecoration: Divider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
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

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[SavedAddressesViewModel::class.java]
        viewModel.updateData()
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
                findNavController().navigate(AddressesFragmentDirections.actionToMapDialogFragment().apply {
                    this.address = addressUI
                })
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

}

enum class AddressType {
    Personal, Company
}

enum class OpenMode {
    SelectAddress
}