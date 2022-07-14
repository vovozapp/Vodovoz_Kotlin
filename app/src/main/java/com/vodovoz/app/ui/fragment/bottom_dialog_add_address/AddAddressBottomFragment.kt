package com.vodovoz.app.ui.fragment.bottom_dialog_add_address

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.data.config.AddressConfig
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.BottomFragmentSortSettingsBinding
import com.vodovoz.app.databinding.DialogBottomAddAddressBinding
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseBottomFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.fragment.cart.CartViewModel
import com.vodovoz.app.ui.fragment.paginated_products_catalog.PaginatedProductsCatalogFragment
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.util.LogSettings

class AddAddressBottomFragment : ViewStateBaseBottomFragment() {

    private lateinit var binding: DialogBottomAddAddressBinding
    private lateinit var viewModel: AddAddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[AddAddressViewModel::class.java]
    }

    private fun getArgs() {
        viewModel.updateArgs(AddAddressBottomFragmentArgs.fromBundle(requireArguments()).address)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = DialogBottomAddAddressBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        onStateSuccess()
        initDialog()
        initSwitchGroup()
        initButtons()
        initFields()
        observeViewModel()
    }

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            binding.root.onRenderFinished { _, height ->
                behavior.peekHeight = height
            }
        }
    }

    private fun initSwitchGroup() {
        binding.isOffice.setOnCheckedChangeListener { _, isCheck ->
            when(isCheck) {
                true -> binding.isPersonalHouse.isChecked = false
                false -> binding.isPersonalHouse.isChecked = true
            }
        }

        binding.isPersonalHouse.setOnCheckedChangeListener { _, isCheck ->
            when(isCheck) {
                true -> binding.isOffice.isChecked = false
                false -> binding.isOffice.isChecked = true
            }
        }
        binding.isPersonalHouse.isChecked = true
    }

    private fun initButtons() {
        binding.addAddress.setOnClickListener {
            viewModel.validate(
                when(binding.isPersonalHouse.isChecked) {
                    true -> AddressConfig.PERSONAL_ADDRESS_TYPE
                    false -> AddressConfig.OFFICE_ADDRESS_TYPE
                }
            )
        }
    }

    private fun initFields() {
        binding.locality.addTextChangedListener { viewModel.locality = it.toString() }
        binding.street.addTextChangedListener { viewModel.street = it.toString() }
        binding.house.addTextChangedListener { viewModel.house = it.toString() }
        binding.entrance.addTextChangedListener { viewModel.entrance = it.toString() }
        binding.floor.addTextChangedListener { viewModel.floor = it.toString() }
        binding.office.addTextChangedListener { viewModel.office = it.toString() }
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            Log.i(LogSettings.ID_LOG,"${state::class.simpleName}")
            when(state) {
                is ViewState.Hide -> onStateHide()
                is ViewState.Error -> {
                    onStateSuccess()
                    Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                }
                is ViewState.Loading -> onStateLoading()
                is ViewState.Success -> {
                    onStateSuccess()
                    findNavController().popBackStack()
                    findNavController().popBackStack()
                }
            }
        }

        viewModel.addressLD.observe(viewLifecycleOwner) { address ->
            binding.locality.setText(address.locality ?: "")
            binding.street.setText(address.street ?: "")
            binding.house.setText(address.house ?: "")
            binding.address.text = address.fullAddress ?: ""
        }

        viewModel.localityLD.observe(viewLifecycleOwner) { error ->
            binding.localityContainer.error = error
        }

        viewModel.streetLD.observe(viewLifecycleOwner) { error ->
            binding.streetContainer.error = error
        }

        viewModel.houseLD.observe(viewLifecycleOwner) { error ->
            binding.houseContainer.error = error
        }

        viewModel.entranceLD.observe(viewLifecycleOwner) { error ->
            binding.entranceContainer.error = error
        }

        viewModel.floorLD.observe(viewLifecycleOwner) { error ->
            binding.floorContainer.error = error
        }

        viewModel.officeLD.observe(viewLifecycleOwner) { error ->
            binding.officeContainer.error = error
        }
    }

    override fun update() {}



}