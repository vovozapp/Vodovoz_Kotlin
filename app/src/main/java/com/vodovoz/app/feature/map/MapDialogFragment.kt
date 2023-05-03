package com.vodovoz.app.feature.map

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.permissions.LocationController
import com.vodovoz.app.databinding.FragmentMapFlowBinding
import com.vodovoz.app.feature.map.adapter.AddressResult
import com.vodovoz.app.feature.map.adapter.AddressResultClickListener
import com.vodovoz.app.feature.map.adapter.AddressResultFlowAdapter
import com.vodovoz.app.ui.extensions.ColorExtensions.getColorWithAlpha
import com.vodovoz.app.ui.model.DeliveryZoneUI
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.snack
import com.yandex.mapkit.*
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.*
import com.yandex.mapkit.geometry.*
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
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class MapDialogFragment : BaseFragment(),
    InputListener,
    UserLocationObjectListener,
    SuggestSession.SuggestListener,
    DrivingSession.DrivingRouteListener {

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

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
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
    private var lastPolyline: PolylineMapObject? = null
    private var isShowSearchResult = false

    private var drivingSession: DrivingSession? = null

    private val distanceToRouteMap = hashMapOf<Double, DrivingRoute>()

    private val mapObjects: MapObjectCollection by lazy {
        binding.mapView.map.mapObjects.addCollection()
    }
    private val drivingRouter: DrivingRouter by lazy {
        DirectionsFactory.getInstance().createDrivingRouter()
    }

    private val locationManager by lazy {
        requireContext().getSystemService(LOCATION_SERVICE) as? LocationManager
    }

    private val locationController by lazy {
        LocationController(requireContext())
    }

    private val addressesResultAdapter = AddressResultFlowAdapter(
        object : AddressResultClickListener {
            override fun onAddressClick(address: AddressResult) {
                binding.searchEdit.setText(address.text)

                search(address.text)
            }

        }
    )

    private fun search(text: String) {
        viewModel.clear()
        searchManager.submit(
            text,
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
                    distanceToRouteMap.clear()
                    viewModel.fetchSeveralMinimalLineDistancesToMainPolygonPoints(point)
                    placeMark(point, com.vodovoz.app.R.drawable.png_map_marker)
                    showContainer(false)
                }

                override fun onSearchError(p0: Error) {

                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        mapKit.onStart()
        binding.mapView.onStart()
        val address = args.address
        if (address == null) {
            if (detectGps()) {
                locationController.methodRequiresTwoPermission(requireActivity()) {
                    moveToLastLocation()
                }
            } else {
                viewModel.changeAddress()
                moveCamera(center)
            }
        } else {
            search(address.fullAddress)
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.updateZones(false)
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
                    if (!state.data.updateZones) {
                        drawDeliveryZones(data.deliveryZonesBundleUI?.deliveryZoneUIList)
                    }

                    val full = state.data.addressUI?.fullAddress?.substringAfter("Россия, ") ?: ""
                    binding.searchEdit.setText(full)
                    binding.streetNameTv.isVisible = true
                    binding.streetNameTv.text = full

                    showError(state.error)
                }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
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
                                submitRequest(point, it.startPoint)
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
                            addPolyline(it.polyline)
                            if (it.message != null) {
                                requireActivity().snack(it.message)
                            }
                        }
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
            override fun onLocationUpdated(p0: Location) {}
            override fun onLocationStatusUpdated(p0: LocationStatus) {}
        })

        binding.plusFrame.setOnClickListener {
            moveCameraPlus()
        }

        binding.minusFrame.setOnClickListener {
            moveCameraMinus()
        }

        binding.infoFrame.setOnClickListener {
            viewModel.showInfoDialog()
        }

        binding.geoFrame.setOnClickListener {

            if (!detectGps()) {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle("Внимание")
                    setMessage("Настройки местоположения должны быть включены, чтобы использовать приложение.")
                    setCancelable(false)
                    setPositiveButton(
                        "Открыть настройки"
                    ) { dialogInterface, i ->
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }
                    show()
                }
            }

            locationController.methodRequiresTwoPermission(requireActivity()) {
                moveToLastLocation()
            }
        }
    }

    private fun moveToLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: android.location.Location? ->
                location?.let {
                    moveCamera(Point(it.latitude, it.longitude), 12f)
                    viewModel.fetchAddressByGeocode(it.latitude, it.longitude)
                }
            }
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

        binding.clear.setOnClickListener {
            binding.searchEdit.text = null
            binding.clear.isVisible = false
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

    private fun moveCamera(point: Point, zoom: Float = 10f) {
        binding.mapView.map.move(
            CameraPosition(point, zoom, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    private fun moveCameraPlus() {
        val currentZoom = binding.mapView.map.cameraPosition.zoom
        if (currentZoom != 16f) {
            binding.mapView.map.move(
                CameraPosition(binding.mapView.map.cameraPosition.target, currentZoom + 1f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
    }

    private fun moveCameraMinus() {
        val currentZoom = binding.mapView.map.cameraPosition.zoom
        if (currentZoom != 16f) {
            binding.mapView.map.move(
                CameraPosition(binding.mapView.map.cameraPosition.target, currentZoom - 1f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
    }

    private fun placeMark(point: Point, resId: Int) {
        lastPlaceMark?.let {
            try {
                binding.mapView.map.mapObjects.remove(it)
            } catch (_: Throwable) {

            }
        }
        lastPlaceMark = binding.mapView.map.mapObjects.addPlacemark(
            point,
            ImageProvider.fromResource(context, resId)
        )
    }

    private fun addPolyline(line: Polyline?) {
        lastPolyline?.let {
            try {
                binding.mapView.map.mapObjects.remove(it)
            } catch (_: Throwable) {

            }
        }
        lastPolyline = if (line != null) {
            binding.mapView.map.mapObjects.addPolyline(line)
        } else {
            null
        }
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
                    com.vodovoz.app.R.drawable.ic_arrow_left
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
                    com.vodovoz.app.R.drawable.ic_search
                )
            )
            binding.clear.isVisible = false
            binding.searchEdit.clearFocus()
            binding.searchContainer.elevation =
                resources.getDimension(com.vodovoz.app.R.dimen.elevation_3)

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
        viewModel.updateZones(true)
    }

    override fun onMapTap(p0: Map, p1: Point) {
        onMapClick(p1)
    }

    override fun onMapLongTap(p0: Map, p1: Point) {
        onMapClick(p1)
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {

        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                requireContext(), com.vodovoz.app.R.drawable.png_gps_1
            ), IconStyle().setScale(0.1f).setRotationType(RotationType.ROTATE).setZIndex(1f)
        )

        val pinIcon: CompositeIcon = userLocationView.pin.useCompositeIcon()

        pinIcon.setIcon(
            "icon",
            ImageProvider.fromResource(requireContext(), com.vodovoz.app.R.drawable.search_result),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(0.5f)
        )

        pinIcon.setIcon(
            "pin",
            ImageProvider.fromResource(requireContext(), com.vodovoz.app.R.drawable.search_result),
            IconStyle()
                .setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(0.5f)
        )

        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
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
        viewModel.clear()
        moveCamera(point)
        placeMark(point, R.drawable.png_map_marker)
        viewModel.fetchAddressByGeocode(
            point.latitude,
            point.longitude
        )
        distanceToRouteMap.clear()
        viewModel.fetchSeveralMinimalLineDistancesToMainPolygonPoints(point)
    }

    private fun initRouteLength(routes: List<Point>): Double {
        var dis = 0.0

        for (i in 0 until routes.size - 1) {
            val it = routes[i]
            dis += Geo.distance(it, routes[i + 1])
        }
        return dis
    }

    override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
        for (route in p0) {
            distanceToRouteMap[initRouteLength(route.geometry.points)] = route
        }

        if (distanceToRouteMap.isNotEmpty()) {
            val key = distanceToRouteMap.minOf { it.key }
            val route = distanceToRouteMap[key]
            val polyline = route?.geometry

            route?.let {
                val startPoint = it.requestPoints!![0].point
                val endPoint = it.requestPoints!![1].point
                viewModel.addPolyline(key, polyline, startPoint, endPoint)
            }
        }
    }

    override fun onDrivingRoutesError(p0: Error) {}

    private fun submitRequest(start: Point, end: Point) {
        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions().apply {

        }
        val requestPoints: ArrayList<RequestPoint> = ArrayList()
        requestPoints.add(
            RequestPoint(
                start,
                RequestPointType.WAYPOINT,
                null
            )
        )
        requestPoints.add(
            RequestPoint(
                end,
                RequestPointType.WAYPOINT,
                null
            )
        )
        drivingSession =
            drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this)
    }

    private fun detectGps(): Boolean {
        val manager = locationManager ?: return false
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}
