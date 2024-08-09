package com.vodovoz.app.feature.map

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.databinding.FragmentMapFlowBinding
import com.vodovoz.app.feature.map.adapter.AddressResult
import com.vodovoz.app.feature.map.adapter.AddressResultClickListener
import com.vodovoz.app.util.extensions.snack
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.user_location.UserLocationLayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MapDialogFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_map_flow

    internal val binding: FragmentMapFlowBinding by viewBinding {
        FragmentMapFlowBinding.bind(
            contentView
        )
    }
    private val viewModel: MapFlowViewModel by viewModels()

    private val args: MapDialogFragmentArgs by navArgs()

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
            binding.mapView,
            binding.plusFrame,
            binding.minusFrame,
            binding.infoFrame,
            binding.geoFrame
        )

        mapController.initSearch(
            binding.addAddress,
            binding.searchEdit,
            binding.searchContainer,
            binding.searchImage,
            binding.clear
        )

        mapController.initAddressesRecycler(
            binding.addressesRecycler
        )

        observeUiState()
        observeEvents()
    }

    override fun onStart() {
        super.onStart()
        mapKit.onStart()
        binding.mapView.onStart()
        val address = args.address
        mapController.onStart(address)
    }

    override fun onStop() {
        binding.mapView.onStop()
        mapKit.onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.updateZones(false)
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

                        val data = state.data
                        if (!state.data.updateZones) {
                            mapController.drawDeliveryZones(data.deliveryZonesBundleUI?.deliveryZoneUIList)
                        }

                        val full =
                            state.data.addressUI?.fullAddress?.substringAfter("Россия, ") ?: ""
                        binding.searchEdit.setText(full)
                        binding.streetNameTv.isVisible = true
                        binding.streetNameTv.text = full

                        showError(state.error)
                    }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            viewModel.observeEvent()
                .collect {
                    when (it) {
                        is MapFlowViewModel.MapFlowEvents.ShowAddAddressBottomDialog -> {
                            findNavController().navigate(
                                MapDialogFragmentDirections.actionToAddAddressBottomFragment(it.address)
                            )
                        }
                        is MapFlowViewModel.MapFlowEvents.Submit -> {
                            it.list.forEach { point ->
                                mapController.submitRequest(point, it.startPoint)
                            }
                        }
                        is MapFlowViewModel.MapFlowEvents.ShowInfoDialog -> {
                            if (it.url.isNullOrEmpty().not()) {
                                findNavController().navigate(
                                    R.id.webViewFlowBottomSheetFragment,
                                    bundleOf(
                                        "title" to "Зоны бесплатных дней доставки за МКАД",
                                        "url" to it.url
                                    )
                                )
                            }
                        }
                        is MapFlowViewModel.MapFlowEvents.ShowAlert -> {
                            MaterialAlertDialogBuilder(requireContext())
                                .setMessage(it.response.string())
                                .setPositiveButton("Ок") { dialog, _ -> dialog.dismiss() }
                                .show()
                        }
                        is MapFlowViewModel.MapFlowEvents.ShowPolyline -> {
                            mapController.addPolyline(it.polyline)
                            if (it.message != null) {
                                requireActivity().snack(it.message)
                            }
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun showContainer(bool: Boolean) {

        binding.appBarLayout.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.white
            )
        )

        if (bool) {
            binding.addAddress.visibility = View.INVISIBLE
            binding.addressesRecycler.visibility = View.VISIBLE
            binding.searchImage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_arrow_left
                )
            )
            binding.clear.isVisible = true
            binding.searchEdit.focusSearch(View.FOCUS_UP)
            binding.searchContainer.elevation = 0f
        } else {
            binding.addAddress.visibility = View.VISIBLE
            binding.addressesRecycler.visibility = View.INVISIBLE
            binding.searchImage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_search
                )
            )
            binding.clear.isVisible = false
            binding.searchEdit.clearFocus()
            binding.searchContainer.elevation =
                resources.getDimension(R.dimen.elevation_3)

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
                binding.searchEdit.setText(address.text)
                mapController.search(address.text)
            }
        }
    }
}
