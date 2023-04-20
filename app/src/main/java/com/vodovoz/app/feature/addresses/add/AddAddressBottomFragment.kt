package com.vodovoz.app.feature.addresses.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.data.config.AddressConfig
import com.vodovoz.app.databinding.BsAddAddressBinding
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseBottomFragment
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.snack
import com.vodovoz.app.util.extensions.textOrError
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddAddressBottomFragment : BaseBottomSheetFragment() {

    override fun layout(): Int = R.layout.bs_add_address

    private val binding: BsAddAddressBinding by viewBinding {
        BsAddAddressBinding.bind(
            contentView
        )
    }

    private val viewModel: AddAddressFlowViewModel by viewModels()

    private val args: AddAddressBottomFragmentArgs by navArgs()

    @Inject
    lateinit var tabManager: TabManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSwitchGroup()
        observeUiState()
        observeEvents()
        initButtons()
        bindTextWatchers()
        initUi()
    }

    private fun initUi() {
        val address = args.address ?: return
        binding.etLocality.setText(address.locality ?: "")
        binding.etStreet.setText(address.street ?: "")
        binding.etHouse.setText(address.house ?: "")
        binding.tvFullAddress.isVisible = address.fullAddress.isNotBlank()
        binding.tvFullAddress.text = address.fullAddress ?: ""
    }

    private fun initButtons() {
        binding.btnAdd.setOnClickListener {
            val type = when(binding.scPersonalHouseDelivery.isChecked) {
                true -> AddressConfig.PERSONAL_ADDRESS_TYPE
                false -> AddressConfig.OFFICE_ADDRESS_TYPE
            }

            val locality = binding.tilLocality.textOrError(FieldValidationsSettings.LOCALITY_LENGTH) ?: return@setOnClickListener
            val street = binding.tilStreet.textOrError(FieldValidationsSettings.STREET_LENGTH) ?: return@setOnClickListener
            val house = binding.tilHouse.textOrError(FieldValidationsSettings.HOUSE_LENGTH) ?: return@setOnClickListener
            val entrance = binding.tilEntrance.textOrError(FieldValidationsSettings.ENTRANCE_LENGTH) ?: return@setOnClickListener
            val floor = binding.tilFloor.textOrError(FieldValidationsSettings.FLOOR_LENGTH) ?: return@setOnClickListener
            val office = binding.tilFlat.textOrError(FieldValidationsSettings.OFFICE_LENGTH) ?: return@setOnClickListener
            val comment = binding.etComment.text.toString()

            val address = args.address
            val lat = address?.latitude ?: ""
            val longitude = address?.longitude ?: ""
            val length = address?.length ?: ""
            val fullAddress = address?.fullAddress?.substringAfter("Россия, ") ?: ""

            viewModel.action(
                locality = locality,
                street = street,
                house = house,
                entrance = entrance,
                floor = floor,
                office = office,
                comment = comment,
                type = type,
                lat = lat,
                longitude = longitude,
                length = length,
                fullAddress = fullAddress
            )

        }

        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

    private fun bindTextWatchers() {
        binding.etLocality.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilLocality.isErrorEnabled = false
        }
        binding.etStreet.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilStreet.isErrorEnabled = false
        }
        binding.etHouse.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilHouse.isErrorEnabled = false
        }
        binding.etEntrance.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilEntrance.isErrorEnabled = false
        }
        binding.etFloor.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilFloor.isErrorEnabled = false
        }
        binding.etFlat.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilFlat.isErrorEnabled = false
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

                    showError(state.error)
                }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeEvent()
                .collect {
                    when(it) {
                        is AddAddressFlowViewModel.AddAddressEvents.AddAddressError -> {
                            requireActivity().snack(it.message)
                        }
                        is AddAddressFlowViewModel.AddAddressEvents.AddAddressSuccess -> {
                            tabManager.setAddressesRefreshState(true)
                            findNavController().popBackStack(R.id.savedAddressesDialogFragment, false)
                            dismiss()
                        }
                    }
                }
        }
    }

    private fun initSwitchGroup() {
        binding.scOfficeDelivery.setOnCheckedChangeListener { _, isCheck ->
            when(isCheck) {
                true -> binding.scPersonalHouseDelivery.isChecked = false
                false -> binding.scPersonalHouseDelivery.isChecked = true
            }
        }

        binding.scPersonalHouseDelivery.setOnCheckedChangeListener { _, isCheck ->
            when(isCheck) {
                true -> binding.scOfficeDelivery.isChecked = false
                false -> binding.scOfficeDelivery.isChecked = true
            }
        }
        binding.scPersonalHouseDelivery.isChecked = true
    }
}
/*
@AndroidEntryPoint
class AddAddressBottomFragment : ViewStateBaseBottomFragment() {

    private lateinit var binding: BsAddAddressBinding
    private val viewModel: AddAddressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        viewModel.updateArgs(AddAddressBottomFragmentArgs.fromBundle(requireArguments()).address)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = BsAddAddressBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        onStateSuccess()
        initSwitchGroup()
        initButtons()
        initFields()
        observeViewModel()
        initBottom()
    }

    private fun initBottom() {
        dialog?.let {
            val behavior = (it as BottomSheetDialog).behavior
            behavior.peekHeight = (resources.displayMetrics.heightPixels/1.3).toInt()
        }
    }

    private fun initSwitchGroup() {
        binding.scOfficeDelivery.setOnCheckedChangeListener { _, isCheck ->
            when(isCheck) {
                true -> binding.scPersonalHouseDelivery.isChecked = false
                false -> binding.scPersonalHouseDelivery.isChecked = true
            }
        }

        binding.scPersonalHouseDelivery.setOnCheckedChangeListener { _, isCheck ->
            when(isCheck) {
                true -> binding.scOfficeDelivery.isChecked = false
                false -> binding.scOfficeDelivery.isChecked = true
            }
        }
        binding.scPersonalHouseDelivery.isChecked = true
    }

    private fun initButtons() {
        binding.btnAdd.setOnClickListener {
            viewModel.validate(
                when(binding.scPersonalHouseDelivery.isChecked) {
                    true -> AddressConfig.PERSONAL_ADDRESS_TYPE
                    false -> AddressConfig.OFFICE_ADDRESS_TYPE
                }
            )
        }

        binding.cancel.setOnClickListener {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = STATE_HIDDEN
        }
    }

    private fun initFields() {
        binding.etLocality.addTextChangedListener { viewModel.locality = it.toString() }
        binding.etStreet.addTextChangedListener { viewModel.street = it.toString() }
        binding.etHouse.addTextChangedListener { viewModel.house = it.toString() }
        binding.etEntrance.addTextChangedListener { viewModel.entrance = it.toString() }
        binding.etFlat.addTextChangedListener { viewModel.floor = it.toString() }
        binding.etFlat.addTextChangedListener { viewModel.office = it.toString() }
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
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
            binding.etLocality.setText(address.locality ?: "")
            binding.etStreet.setText(address.street ?: "")
            binding.etHouse.setText(address.house ?: "")
            binding.tvFullAddress.isVisible = address.fullAddress.isNotBlank()
            binding.tvFullAddress.text = address.fullAddress ?: ""
        }

        viewModel.localityLD.observe(viewLifecycleOwner) { error ->
            binding.tilLocality.error = error
        }

        viewModel.streetLD.observe(viewLifecycleOwner) { error ->
            binding.tilStreet.error = error
        }

        viewModel.houseLD.observe(viewLifecycleOwner) { error ->
            binding.tilHouse.error = error
        }

        viewModel.entranceLD.observe(viewLifecycleOwner) { error ->
            binding.tilEntrance.error = error
        }

        viewModel.floorLD.observe(viewLifecycleOwner) { error ->
            binding.tilFloor.error = error
        }

        viewModel.officeLD.observe(viewLifecycleOwner) { error ->
            binding.tilFlat.error = error
        }
    }

    override fun update() {}



}*/
