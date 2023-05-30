package com.vodovoz.app.feature.all.orders.detail.traceorder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.databinding.FragmentTraceOrderBinding
import com.vodovoz.app.databinding.FragmentTraceOrderBottomBinding
import com.vodovoz.app.feature.map.MapDialogFragmentArgs
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
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class TraceOrderFragment : BaseFragment(), InputListener,
    UserLocationObjectListener {

    override fun layout(): Int = R.layout.fragment_trace_order

    private val binding: FragmentTraceOrderBinding by viewBinding {
        FragmentTraceOrderBinding.bind(contentView)
    }

    private val viewModel: TraceOrderViewModel by activityViewModels()

    private val args: TraceOrderFragmentArgs by navArgs()

    private val userLocationLayer: UserLocationLayer by lazy { mapKit.createUserLocationLayer(binding.mapView.mapWindow) }
    private val mapKit: MapKit by lazy { MapKitFactory.getInstance() }
    private val permissionsController by lazy { PermissionsController(requireContext()) }
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(requireActivity()) }
    private val locationManager by lazy { requireContext().getSystemService(Context.LOCATION_SERVICE) as? LocationManager }
    private val center = Point(55.75, 37.62)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar("Где мой заказ?")
        initMap()
        observeUiState()

    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect {

                }
        }
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
                    ) { dialogInterface, i ->
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
                context, com.vodovoz.app.R.drawable.png_gps_1
            ), IconStyle().setScale(0.1f).setRotationType(RotationType.ROTATE).setZIndex(1f)
        )

        val pinIcon: CompositeIcon = userLocationView.pin.useCompositeIcon()

        pinIcon.setIcon(
            "icon",
            ImageProvider.fromResource(context, com.vodovoz.app.R.drawable.search_result),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(0.5f)
        )

        pinIcon.setIcon(
            "pin",
            ImageProvider.fromResource(context, com.vodovoz.app.R.drawable.search_result),
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
}