package com.vodovoz.app.ui.components.fragment.bottom_dialog_add_address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.BottomFragmentSortSettingsBinding
import com.vodovoz.app.databinding.DialogBottomAddAddressBinding
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseBottomFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.fragment.cart.CartViewModel
import com.vodovoz.app.ui.components.fragment.paginated_products_catalog.PaginatedProductsCatalogFragment
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished
import com.vodovoz.app.ui.model.AddressUI

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
        initDialog()
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


    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> onStateHide()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Loading -> onStateLoading()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.addressLD.observe(viewLifecycleOwner) { address ->
            binding.locality.setText(address.locality ?: "")
            binding.street.setText(address.street ?: "")
            binding.house.setText(address.house ?: "")
            binding.address.text = address.fullAddress ?: ""
        }
    }

    override fun update() {}



}