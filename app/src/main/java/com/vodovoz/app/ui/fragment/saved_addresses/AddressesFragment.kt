package com.vodovoz.app.ui.fragment.saved_addresses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
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
import com.vodovoz.app.ui.diffUtils.AddressDiffUtilCallback
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.model.AddressUI

class AddressesFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentAddressesBinding
    private lateinit var viewModel: SavedAddressesViewModel

    private val addressesAdapter = AddressesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
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
        binding.rvAddresses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddresses.adapter = addressesAdapter
        val space16 = resources.getDimension(R.dimen.space_16).toInt()
        val spaceLastItemBottomSpace = resources.getDimension(R.dimen.last_item_bottom_normal_space).toInt()
        binding.rvAddresses.addMarginDecoration { rect, view, parent, state ->
            rect.left = space16
            rect.right = space16
            rect.top = space16
            val position = parent.getChildAdapterPosition(view)
            if (parent.adapter?.getItemViewType(position) == AddressesAdapterItemType.ADDRESS.value) {
                if (position == state.itemCount - 1) rect.bottom = spaceLastItemBottomSpace
                else rect.bottom = space16
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
        if (pair.first.isNotEmpty()) {
            itemList.add(AddressesAdapterItem.AddressesTypeTitle(resources.getString(R.string.personal_addresses_title)))
            itemList.addAll(pair.first.map { AddressesAdapterItem.Address(it) })
        }
        if (pair.second.isNotEmpty()) {
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