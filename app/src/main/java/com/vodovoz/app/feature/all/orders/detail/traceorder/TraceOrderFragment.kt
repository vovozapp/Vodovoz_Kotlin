package com.vodovoz.app.feature.all.orders.detail.traceorder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.databinding.FragmentTraceOrderBinding
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.drawable
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.traffic.TrafficColor
import com.yandex.mapkit.traffic.TrafficLevel
import com.yandex.mapkit.traffic.TrafficListener
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TraceOrderFragment : BaseFragment(), InputListener,
    UserLocationObjectListener, TrafficListener {

    override fun layout(): Int = R.layout.fragment_trace_order

    private val binding: FragmentTraceOrderBinding by viewBinding {
        FragmentTraceOrderBinding.bind(contentView)
    }

    private val viewModel: TraceOrderViewModel by activityViewModels()

    private val args: TraceOrderFragmentArgs by navArgs()

    private val userLocationLayer: UserLocationLayer by lazy {
        mapKit.createUserLocationLayer(
            binding.mapView.mapWindow
        )
    }
    private val mapKit: MapKit by lazy { MapKitFactory.getInstance() }
    private val permissionsController by lazy { PermissionsController(requireContext()) }
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            requireActivity()
        )
    }
    private val locationManager by lazy { requireContext().getSystemService(Context.LOCATION_SERVICE) as? LocationManager }
    private val center = Point(55.75, 37.62)
    private val traffic by lazy { mapKit.createTrafficLayer(binding.mapView.mapWindow) }

    private enum class TrafficState { LOADING, STARTED, EXPIRED }

    private var lastPlaceMark: PlacemarkMapObject? = null

    override fun onStart() {
        super.onStart()
        mapKit.onStart()
        binding.mapView.onStart()
        if (detectGps()) {
            permissionsController.methodRequiresLocationsPermission(requireActivity()) {
                moveToLastLocation()
            }
        } else {
            moveCamera(center)
        }
    }

    override fun onStop() {
        binding.mapView.onStop()
        mapKit.onStop()
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        debugLog { "args ${args.driverId}" }
        viewModel.fetchDriverData(args.driverId, args.orderId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar("Где мой заказ?")
        initMap()
        initTraffic()
        observeUiState()
        initBottomSheetCallback()
        bindBottomSheetBtns()
    }

    private fun bindBottomSheetBtns() {
        binding.traceOrderBs.callUsBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+74959213434", null))
            startActivity(intent)
        }

        binding.traceOrderBs.chatUsBtn.setOnClickListener {
            findNavController().navigate(
                TraceOrderFragmentDirections.actionToWebViewFragment(
                    "http://jivo.chat/mk31km1IlP",
                    "Чат"
                )
            )
        }
    }

    private fun initBottomSheetCallback() {
        val behavior = BottomSheetBehavior.from(binding.traceOrderBs.root)
        val density = requireContext().resources.displayMetrics.density
        behavior.peekHeight = (115 * density).toInt()
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        behavior.isHideable = false
    }

    private fun initTraffic() {
        traffic.isTrafficVisible = true
        traffic.addTrafficListener(this)
        binding.trafficFrame.setOnClickListener {
            traffic.isTrafficVisible = !traffic.isTrafficVisible
            updateLevel(TrafficState.LOADING)
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect {

                    if (it.data.driverPoint != null && it.data.autoBitmap != null) {
                        moveCamera(it.data.driverPoint)
                        placeMark(it.data.driverPoint, it.data.autoBitmap)
                    }

                    val driverEntity = it.data.driverPointsEntity

                    if (driverEntity != null && it.data.homeBitmap != null) {
                        val lat = driverEntity.Position?.Latitude
                        val lon = driverEntity.Position?.Longitude
                        if (!lat.isNullOrEmpty() && !lon.isNullOrEmpty()) {
                            placeMark(Point(lat.toDouble(), lon.toDouble()), it.data.homeBitmap)
                        }
                    }

                    binding.traceOrderBs.driverNameTv.text = it.data.name ?: ""
                    binding.traceOrderBs.carNumberTv.text = it.data.car ?: ""

                    if (driverEntity != null) {
                        if (driverEntity.DriverDirection == "TRUE") {
                            binding.traceOrderBs.timeTv.isVisible = false
                            binding.traceOrderBs.commentTv.isVisible = true
                            binding.traceOrderBs.commentTv.text =
                                "Водитель выехал и направляется к Вам."
                        } else {
                            binding.traceOrderBs.timeTv.isVisible = true
                            binding.traceOrderBs.commentTv.isVisible = false
                            binding.traceOrderBs.timeTv.text =
                                buildString {
                                    append("Ориентировочное время прибытия: ")
                                    append(it.data.driverPointsEntity.Priblizitelnoe_vremya)
                                }
                        }
                    } else {
                        binding.traceOrderBs.timeTv.isVisible = false
                        binding.traceOrderBs.commentTv.isVisible = false
                    }
                }
        }
    }

    private fun placeMark(point: Point, bitmap: Bitmap) {
        binding.mapView.map.mapObjects.addPlacemark(
            point,
            ImageProvider.fromBitmap(bitmap)
        )
    }

    private fun initMap() {

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

        binding.geoFrame.setOnClickListener {
            if (!detectGps()) {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle("Внимание")
                    setMessage("Настройки местоположения должны быть включены, чтобы использовать приложение.")
                    setCancelable(false)
                    setPositiveButton(
                        "Открыть настройки"
                    ) { _, _ ->
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    }
                    show()
                }
            }

            permissionsController.methodRequiresLocationsPermission(requireActivity()) {
                moveToLastLocation()
            }
        }
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
                }
            }
    }

    private fun detectGps(): Boolean {
        val manager = locationManager ?: return false
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onMapTap(p0: Map, p1: Point) {}
    override fun onMapLongTap(p0: Map, p1: Point) {}
    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                context, R.drawable.png_gps_1
            ), IconStyle().setScale(0.1f).setRotationType(RotationType.ROTATE).setZIndex(1f)
        )

        val pinIcon: CompositeIcon = userLocationView.pin.useCompositeIcon()

        pinIcon.setIcon(
            "icon",
            ImageProvider.fromResource(context, R.drawable.search_result),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(0.5f)
        )

        pinIcon.setIcon(
            "pin",
            ImageProvider.fromResource(context, R.drawable.search_result),
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

    override fun onTrafficChanged(p0: TrafficLevel?) {
        updateLevel(TrafficState.STARTED, p0)
    }

    override fun onTrafficLoading() {
        updateLevel(TrafficState.LOADING)
    }

    override fun onTrafficExpired() {
        updateLevel(TrafficState.EXPIRED)
    }

    private fun updateLevel(state: TrafficState, level: TrafficLevel? = null) {

        if (!traffic.isTrafficVisible) {
            binding.trafficIv.setImageDrawable(requireContext().drawable(R.drawable.icon_traffic_light_dark))
            return
        }

        when (state) {
            TrafficState.LOADING -> {
                binding.trafficIv.setImageDrawable(requireContext().drawable(R.drawable.icon_traffic_light_grey))
            }
            TrafficState.STARTED -> {
                if (level == null) {
                    binding.trafficIv.setImageDrawable(requireContext().drawable(R.drawable.icon_traffic_light_grey))
                } else {
                    val icon = when (level.color) {
                        TrafficColor.RED -> R.drawable.icon_traffic_light_red
                        TrafficColor.GREEN -> R.drawable.icon_traffic_light_green
                        TrafficColor.YELLOW -> R.drawable.icon_traffic_light_yellow
                        else -> R.drawable.icon_traffic_light_grey
                    }
                    binding.trafficIv.setImageDrawable(requireContext().drawable(icon))
                    binding.trafficTv.text = level.level.toString()
                }
            }
            TrafficState.EXPIRED -> {
                binding.trafficIv.setImageDrawable(requireContext().drawable(R.drawable.icon_traffic_light_blue))
            }
        }
    }
}