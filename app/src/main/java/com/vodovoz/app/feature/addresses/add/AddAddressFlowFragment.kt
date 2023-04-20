package com.vodovoz.app.feature.addresses.add

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.data.config.AddressConfig
import com.vodovoz.app.databinding.BsAddAddressBinding
import com.vodovoz.app.ui.fragment.bottom_dialog_add_address.AddAddressBottomFragmentArgs
import com.vodovoz.app.ui.fragment.bottom_dialog_add_address.AddAddressViewModel
import com.vodovoz.app.util.FieldValidationsSettings.ENTRANCE_LENGTH
import com.vodovoz.app.util.FieldValidationsSettings.FLOOR_LENGTH
import com.vodovoz.app.util.FieldValidationsSettings.HOUSE_LENGTH
import com.vodovoz.app.util.FieldValidationsSettings.LOCALITY_LENGTH
import com.vodovoz.app.util.FieldValidationsSettings.OFFICE_LENGTH
import com.vodovoz.app.util.FieldValidationsSettings.STREET_LENGTH
import com.vodovoz.app.util.extensions.snack
import com.vodovoz.app.util.extensions.textOrError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddAddressFlowFragment : BaseBottomSheetFragment() {

    override fun layout(): Int = R.layout.bs_add_address

    private val binding: BsAddAddressBinding by viewBinding {
        BsAddAddressBinding.bind(
            contentView
        )
    }

    private val viewModel: AddAddressFlowViewModel by viewModels()

    private val args: AddAddressBottomFragmentArgs by navArgs()

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

            val locality = binding.tilLocality.textOrError(LOCALITY_LENGTH) ?: return@setOnClickListener
            val street = binding.tilStreet.textOrError(STREET_LENGTH) ?: return@setOnClickListener
            val house = binding.tilHouse.textOrError(HOUSE_LENGTH) ?: return@setOnClickListener
            val entrance = binding.tilEntrance.textOrError(ENTRANCE_LENGTH) ?: return@setOnClickListener
            val floor = binding.tilFloor.textOrError(FLOOR_LENGTH) ?: return@setOnClickListener
            val office = binding.tilFlat.textOrError(OFFICE_LENGTH) ?: return@setOnClickListener
            val comment = binding.etComment.text.toString()

            viewModel.action(
                locality = locality,
                street = street,
                house = house,
                entrance = entrance,
                floor = floor,
                office = office,
                comment = comment,
                type = type
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
                        is AddAddressFlowViewModel.AddAddressEvents.AddAddressEvent -> {
                            requireActivity().snack(it.message)
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