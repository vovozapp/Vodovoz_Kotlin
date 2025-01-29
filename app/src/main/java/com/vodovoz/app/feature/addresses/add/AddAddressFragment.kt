package com.vodovoz.app.feature.addresses.add

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.data.config.AddressConfig
import com.vodovoz.app.databinding.FragmentAddAddressSearchBinding
import com.vodovoz.app.feature.map.MapController
import com.vodovoz.app.feature.map.MapFlowViewModel
import com.vodovoz.app.feature.map.adapter.AddressResult
import com.vodovoz.app.feature.map.adapter.AddressResultClickListener
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.extensions.snack
import com.vodovoz.app.util.extensions.textOrError
import com.vodovoz.app.util.extensions.updateText
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.user_location.UserLocationLayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddAddressFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_add_address_search

    internal val binding: FragmentAddAddressSearchBinding by viewBinding {
        FragmentAddAddressSearchBinding.bind(
            contentView
        )
    }

    private val viewModel: MapFlowViewModel by viewModels()

    @Inject
    lateinit var tabManager: TabManager

    private val userLocationLayer: UserLocationLayer by lazy {
        mapKit.createUserLocationLayer(binding.mapView.mapWindow)
    }

    private val mapKit: MapKit by lazy {
        MapKitFactory.getInstance()
    }

    @Inject
    lateinit var permissionsControllerFactory: PermissionsController.Factory

    internal val mapController by lazy {
        MapController(
            mapKit,
            fetchAddressResultClickListener(),
            userLocationLayer,
            viewModel,
            requireContext(),
            requireActivity(),
            permissionsControllerFactory
        ) {
            showContainer(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapController.initMap(
            binding.mapView
        )

        //mapController.initSearch(
        //    null,
        //    binding.searchEdit,
        //    binding.searchContainer,
        //    binding.searchImage,
        //    binding.clear,
        //    binding.tvFullAddress,
        //    binding.addressesRecycler
        //)
//
        //mapController.initAddressesRecycler(
        //    binding.addressesRecycler
        //)

        observeUiState()
        observeEvents()
        initButtons()
        bindTextWatchers()
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

    private fun buildAddressFields(address: AddressUI?) {
        //if (address == null) return
        //binding.searchEdit.updateText(address.fullAddress.substringAfter("Россия, "))
        //if (address.fullAddress.isNotBlank()) {
        //    binding.tvFullAddress.visibility = View.VISIBLE
        //} else {
        //    binding.tvFullAddress.visibility = View.INVISIBLE
        //}
        //binding.tvFullAddress.text = address.fullAddress.substringAfter("Россия, ")
        if (address != null) {
            binding.etEntrance.setText(address.entrance)
            binding.etFloor.setText(address.floor)
            binding.etFlat.setText(address.flat)
            binding.etIntercom.setText(address.intercom)
            Log.d("rserbgdrtbgh", address.entrance)
        }
    }

    private fun initButtons() {
        binding.btnAdd.setOnClickListener {
            val type = when (binding.cbHouse.isChecked) {
                true -> AddressConfig.PERSONAL_ADDRESS_TYPE
                false -> AddressConfig.OFFICE_ADDRESS_TYPE
            }

            val entrance = binding.etEntrance.text.toString()
            val floor = binding.etFloor.text.toString()
            val office = binding.etFlat.text.toString()
            val intercom = binding.etIntercom.text.toString()

            viewModel.action(
                entrance = entrance,
                floor = floor,
                office = office,
                intercom = intercom,
                type = type
            )

        }
    }

    private fun bindTextWatchers() {
        //binding.searchEdit.doOnTextChanged { _, _, _, count ->
        //    if (count > 0) {
        //        binding.clear.isVisible = true
        //        binding.tvFullAddress.isVisible = true
        //    }
        //}

        binding.etEntrance.doOnTextChanged { _, _, _, count ->

        }
        binding.etFloor.doOnTextChanged { _, _, _, count ->

        }
        binding.etFlat.doOnTextChanged { _, _, _, count ->

        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUiState()
                    .collect { state ->

                        if (state.loadingPage) {
                            showLoader()
                        } else {
                            hideLoader()
                        }

                        buildAddressFields(state.data.addressUI)
                        showError(state.error)
                    }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeEvent()
                    .collect {
                        when (it) {
                            is MapFlowViewModel.MapFlowEvents.AddAddressError -> {
                                requireActivity().snack(it.message)
                            }
                            is MapFlowViewModel.MapFlowEvents.AddAddressSuccess -> {
                                tabManager.setAddressesRefreshState(true)
                                findNavController().popBackStack(
                                    R.id.savedAddressesDialogFragment,
                                    false
                                )
                            }
                            is MapFlowViewModel.MapFlowEvents.ShowSearchError -> {
                            }
                            is MapFlowViewModel.MapFlowEvents.Submit -> {
                                it.list.forEach { point ->
                                    mapController.submitRequest(point, it.startPoint)
                                }
                            }
                            else -> {}
                        }
                    }
            }
        }
    }

    private fun showContainer(bool: Boolean) {

        if (bool) {
            //binding.addressesRecycler.visibility = View.VISIBLE
//
            //binding.clear.isVisible = true
            //binding.searchEdit.focusSearch(View.FOCUS_UP)
            //binding.searchContainer.elevation = 0f
        } else {
            //binding.addressesRecycler.visibility = View.GONE
//
            //binding.clear.isVisible = false
            //binding.searchEdit.clearFocus()
            //binding.searchContainer.elevation = resources.getDimension(R.dimen.elevation_3)

            val view = requireActivity().currentFocus
            if (view != null) {
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun fetchAddressResultClickListener(): AddressResultClickListener {
        return object : AddressResultClickListener {
            override fun onAddressClick(address: AddressResult) {
                //binding.searchEdit.setText(address.text)
                mapController.search(address.text)
            }
        }
    }
}
