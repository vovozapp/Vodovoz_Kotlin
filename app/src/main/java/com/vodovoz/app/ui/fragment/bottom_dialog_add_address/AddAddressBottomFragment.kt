package com.vodovoz.app.ui.fragment.bottom_dialog_add_address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.data.config.AddressConfig
import com.vodovoz.app.databinding.BsAddAddressBinding
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseBottomFragment
import dagger.hilt.android.AndroidEntryPoint

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
            behavior.peekHeight = (resources.displayMetrics.heightPixels/1.4).toInt()
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
         //   binding.tvFullAddress.text = address.fullAddress ?: ""
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



}