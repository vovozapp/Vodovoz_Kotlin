package com.vodovoz.app.feature.map.old

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentMapFlowBinding
import com.vodovoz.app.feature.map.MapDialogFragmentArgs
import com.vodovoz.app.feature.map.MapDialogFragmentDirections
import com.vodovoz.app.feature.map.MapFlowViewModel
import com.vodovoz.app.feature.map.adapter.AddressResult
import com.vodovoz.app.feature.map.adapter.AddressResultClickListener
import com.vodovoz.app.feature.map.adapter.AddressResultFlowAdapter
import com.vodovoz.app.ui.extensions.ColorExtensions.getColorWithAlpha
import com.vodovoz.app.ui.model.DeliveryZoneUI
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.*
import com.yandex.mapkit.search.Session.SearchListener
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFlowFragment : BaseFragment(),
    InputListener,
    UserLocationObjectListener,
    SuggestSession.SuggestListener {

    override fun layout(): Int = R.layout.fragment_map_flow

    private val binding: FragmentMapFlowBinding by viewBinding {
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

    private val searchManager: SearchManager by lazy {
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    }
    private val suggestSession: SuggestSession by lazy {
        searchManager.createSuggestSession()
    }

    private val center = Point(55.75, 37.62)
    private val boxSize = 0.2
    private val boundingBox = BoundingBox(
        Point(center.latitude - boxSize, center.longitude - boxSize),
        Point(center.latitude + boxSize, center.longitude + boxSize)
    )
    private val searchOptions = SuggestOptions().setSuggestTypes(
        SuggestType.GEO.value or
                SuggestType.BIZ.value or
                SuggestType.TRANSIT.value
    )

    private var lastPlaceMark: PlacemarkMapObject? = null
    private var isShowSearchResult = false
    private var isEditOldAddressMode = false

    private val addressesResultAdapter = AddressResultFlowAdapter(
        object : AddressResultClickListener {
            override fun onAddressClick(address: AddressResult) {
                binding.searchEdit.setText(address.text)

                searchManager.submit(
                    address.text,
                    VisibleRegionUtils.toPolygon(binding.mapView.map.visibleRegion),
                    SearchOptions(),
                    object : SearchListener {
                        override fun onSearchResponse(p0: Response) {

                            val point = Point(
                                p0.collection.children[0].obj?.geometry?.get(0)?.point?.latitude!!,
                                p0.collection.children[0].obj?.geometry?.get(0)?.point?.longitude!!
                            )

                            viewModel.fetchAddressByGeocode(point.latitude, point.longitude)
                            moveCamera(point)
                            placeMark(point, R.drawable.png_map_marker)
                            showContainer(false)
                        }

                        override fun onSearchError(p0: Error) {

                        }
                    }
                )
            }

        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isEditOldAddressMode = true
    }

    override fun onStart() {
        super.onStart()
        mapKit.onStart()
        binding.mapView.onStart()
        moveCamera(center)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        initSearch()
        initAddressesRecycler()
        observeUiState()
        observeEvents()
    }

    override fun onStop() {
        binding.mapView.onStop()
        mapKit.onStop()
        super.onStop()
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

                    val data = state.data
                    drawDeliveryZones(data.deliveryZonesBundleUI?.deliveryZoneUIList)

                    val searchText = state.data.addressUI?.fullAddress ?:""
                    binding.searchEdit.setText(searchText)

                    showError(state.error)
                }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeEvent()
                .collect {
                    when(it) {
                        is MapFlowViewModel.MapFlowEvents.ShowAddAddressBottomDialog -> {
                            findNavController().navigate(
                                MapDialogFragmentDirections.actionToAddAddressBottomFragment(it.address)
                            )
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun initMap() {
        binding.mapView.map.addInputListener(this)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.setObjectListener(this)

        mapKit.createLocationManager().requestSingleUpdate(object : LocationListener {
            override fun onLocationUpdated(p0: Location) {
                Toast.makeText(
                    requireContext(),
                    "${p0.position.latitude} ${p0.position.longitude}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onLocationStatusUpdated(p0: LocationStatus) {
                Toast.makeText(requireContext(), "UPDATE", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun initSearch() {

        binding.addAddress.setOnClickListener {
            viewModel.showAddAddressBottomDialog()
        }

        binding.searchEdit.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(query: Editable?) {
                    searchAddresses(query.toString())
                }
            }
        )

        binding.searchContainer.setOnClickListener { showContainer(true) }
        binding.searchEdit.setOnFocusChangeListener { _, isFocus ->
            if (isFocus) showContainer(true)
        }
        binding.searchImage.setOnClickListener {
            if (isShowSearchResult) {
                binding.searchEdit.text = null
                showContainer(false)
            } else {
                showContainer(true)
            }
        }
    }

    private fun initAddressesRecycler() {
        binding.addressesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.addressesRecycler.adapter = addressesResultAdapter
        binding.addressesRecycler.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun searchAddresses(query: String) {
        suggestSession.suggest(query, boundingBox, searchOptions, this)
    }

    private fun moveCamera(point: Point) {
        binding.mapView.map.move(
            CameraPosition(point, 16f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    private fun placeMark(point: Point, resId: Int) {
        lastPlaceMark?.let { binding.mapView.map.mapObjects.remove(it) }
        lastPlaceMark = binding.mapView.map.mapObjects.addPlacemark(
            point,
            ImageProvider.fromResource(context, resId)
        )
    }

    private fun showContainer(bool: Boolean) {
        isShowSearchResult = bool

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
            binding.searchEdit.clearFocus()
            binding.searchContainer.elevation = resources.getDimension(R.dimen.elevation_3)

            val view = requireActivity().currentFocus
            if (view != null) {
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun drawDeliveryZones(deliveryZoneUIList: List<DeliveryZoneUI>?) {
        if (deliveryZoneUIList == null) return

        deliveryZoneUIList.forEach { deliveryZoneUI ->
            val zone = binding.mapView.map.mapObjects.addPolygon(
                Polygon(LinearRing(deliveryZoneUI.pointList), ArrayList())
            )
            zone.fillColor = Color.parseColor(deliveryZoneUI.color).getColorWithAlpha(0.4f)
            zone.strokeWidth = 0.0f
            zone.zIndex = 100.0f
        }
    }

    override fun onMapTap(p0: Map, p1: Point) {
        onMapClick(p1)
    }

    override fun onMapLongTap(p0: Map, p1: Point) {
        onMapClick(p1)
    }

    override fun onObjectAdded(p0: UserLocationView) {
        /*binding.mapView.map.move(
            CameraPosition(
                Point(
                    p0.arrow.geometry.longitude,
                    p0.arrow.geometry.latitude
                ), 16f, 0f, 0f
            ),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )*/
    }

    override fun onObjectRemoved(p0: UserLocationView) {}
    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}

    override fun onResponse(p0: MutableList<SuggestItem>) {
        val addressList = mutableListOf<AddressResult>()
        for (item in p0) {
            if (item.uri?.contains("geo") == true) {
                val coordinates = item.uri?.split("&")?.first()?.split("=")?.get(1)?.split("%2C")
                addressList.add(
                    AddressResult(
                        item.displayText.toString(),
                        Point(
                            coordinates?.last()?.toDoubleOrNull() ?: 0.0,
                            coordinates?.first()?.toDoubleOrNull() ?: 0.0
                        )
                    )
                )
            }
        }
        addressesResultAdapter.submitList(addressList)
    }

    override fun onError(p0: Error) {}

    private fun onMapClick(point: Point) {
        moveCamera(point)
        placeMark(point, R.drawable.png_map_marker)
        viewModel.fetchAddressByGeocode(
            point.latitude,
            point.longitude
        )
    }
}