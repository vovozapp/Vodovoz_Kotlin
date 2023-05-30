package com.vodovoz.app.feature.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.location.LocationManager
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.vodovoz.app.R
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.feature.map.adapter.AddressResult
import com.vodovoz.app.feature.map.adapter.AddressResultClickListener
import com.vodovoz.app.feature.map.adapter.AddressResultFlowAdapter
import com.vodovoz.app.ui.extensions.ColorExtensions.getColorWithAlpha
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.ui.model.DeliveryZoneUI
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
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import java.util.ArrayList

class MapController(
    private val mapKit: MapKit,
    private val addressResultClickListener: AddressResultClickListener,
    private val userLocationLayer: UserLocationLayer,
    private val viewModel: MapFlowViewModel,
    private val context: Context,
    private val activity: FragmentActivity,
    private val showContainer: (Boolean) -> Unit
) : InputListener,
    UserLocationObjectListener,
    SuggestSession.SuggestListener,
    DrivingSession.DrivingRouteListener {

    private val locationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    }

    private val permissionsController by lazy {
        PermissionsController(context)
    }

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(activity)
    }

    private val searchManager: SearchManager by lazy {
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    }
    private val suggestSession: SuggestSession by lazy {
        searchManager.createSuggestSession()
    }

    private val addressesResultAdapter = AddressResultFlowAdapter(addressResultClickListener)

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

    private val drivingRouter: DrivingRouter by lazy {
        DirectionsFactory.getInstance().createDrivingRouter()
    }

    private lateinit var mapView: MapView

    /**
     * init MAP, init map buttons
     */

    fun initMap(
        mapView: MapView,
        plusFrame: ShapeableImageView? = null,
        minusFrame: ShapeableImageView? = null,
        infoFrame: FrameLayout? = null,
        geoFrame: FrameLayout? = null
    ) {
        this.mapView = mapView

        mapView.map.addInputListener(this)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.setObjectListener(this)

        mapKit.createLocationManager().requestSingleUpdate(object : LocationListener {
            override fun onLocationUpdated(p0: Location) {}
            override fun onLocationStatusUpdated(p0: LocationStatus) {}
        })

        plusFrame?.setOnClickListener {
            moveCameraPlus()
        }

        minusFrame?.setOnClickListener {
            moveCameraMinus()
        }

        infoFrame?.setOnClickListener {
            viewModel.showInfoDialog()
        }

        geoFrame?.setOnClickListener {

            if (!detectGps()) {
                MaterialAlertDialogBuilder(context).apply {
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

            permissionsController.methodRequiresLocationsPermission(activity) {
                moveToLastLocation()
            }
        }
    }
    /*************************/

    /**
     * Camera move
     */
    private fun moveToLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
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

    private fun moveCamera(point: Point, zoom: Float = 10f) {
        mapView.map.move(
            CameraPosition(point, zoom, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    private fun moveCameraPlus() {
        val currentZoom = mapView.map.cameraPosition.zoom
        if (currentZoom != 16f) {
            mapView.map.move(
                CameraPosition(mapView.map.cameraPosition.target, currentZoom + 1f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
    }

    private fun moveCameraMinus() {
        val currentZoom = mapView.map.cameraPosition.zoom
        if (currentZoom != 16f) {
            mapView.map.move(
                CameraPosition(mapView.map.cameraPosition.target, currentZoom - 1f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
    }
    /*************************/

    /**
     * init SEARCH
     */

    fun initSearch(
        addAddress: TextView? = null,
        searchEditText: EditText? = null,
        searchContainer: LinearLayout? = null,
        searchImage: ImageView? = null,
        clear: ImageView? = null,
        fullAddressTextView: TextView? = null,
        searchErrorTv: TextView? = null,
        addressesRecycler: RecyclerView? = null
    ) {

        addAddress?.setOnClickListener {
            viewModel.showAddAddressBottomDialog()
        }

        searchEditText?.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(query: Editable?) {
                    if (searchEditText.hasFocus()) {
                        addressesRecycler?.isVisible = true
                        searchAddresses(query.toString())
                    }
                }
            }
        )

        searchContainer?.setOnClickListener {
            isShowSearchResult = true
            showContainer(true)
        }
        searchEditText?.setOnFocusChangeListener { _, isFocus ->
            if (isFocus) {
                isShowSearchResult = true
                showContainer(true)
            }
        }
        searchImage?.setOnClickListener {
            if (isShowSearchResult) {
                searchEditText?.text = null
                isShowSearchResult = false
                showContainer(false)
            } else {
                isShowSearchResult = true
                showContainer(true)
            }
        }

        clear?.setOnClickListener {
            searchEditText?.text = null
            clear.isVisible = false
            searchErrorTv?.isVisible = false
            addressesResultAdapter.submitList(emptyList())
            addressesRecycler?.isVisible = false
            fullAddressTextView?.text = null
            fullAddressTextView?.visibility = View.INVISIBLE
        }
    }

    fun search(text: String) {
        viewModel.clear()
        searchManager.submit(
            text,
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            SearchOptions(),
            object : Session.SearchListener {
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
                    isShowSearchResult = false
                    showContainer(false)
                }

                override fun onSearchError(p0: Error) {

                }
            }
        )
    }

    fun searchForUpdate(address: AddressUI) {
        viewModel.clear()
        searchManager.submit(
            address.fullAddress,
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            SearchOptions(),
            object : Session.SearchListener {
                override fun onSearchResponse(p0: Response) {

                    val point = Point(
                        p0.collection.children[0].obj?.geometry?.get(0)?.point?.latitude!!,
                        p0.collection.children[0].obj?.geometry?.get(0)?.point?.longitude!!
                    )

                    distanceToRouteMap.clear()
                    viewModel.fetchSeveralMinimalLineDistancesToMainPolygonPoints(point, address)
                }

                override fun onSearchError(p0: Error) {}
            }
        )
    }
    /*************************/

    /**
     * add map objects -> UserLocationObjectListener
     */

    private fun placeMark(point: Point, resId: Int) {
        lastPlaceMark?.let {
            try {
                mapView.map.mapObjects.remove(it)
            } catch (_: Throwable) {

            }
        }
        lastPlaceMark = mapView.map.mapObjects.addPlacemark(
            point,
            ImageProvider.fromResource(context, resId)
        )
    }

    fun addPolyline(line: Polyline?) {
        lastPolyline?.let {
            try {
                mapView.map.mapObjects.remove(it)
            } catch (_: Throwable) {

            }
        }
        lastPolyline = if (line != null) {
            mapView.map.mapObjects.addPolyline(line)
        } else {
            null
        }
    }

    /*************************/

    /**
     * InputListener
     */

    override fun onMapTap(p0: Map, p1: Point) {
        onMapClick(p1)
    }

    override fun onMapLongTap(p0: Map, p1: Point) {
        onMapClick(p1)
    }

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
    /*************************/

    /**
     * UserLocationObjectListener
     */

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

    /**
     * SuggestListener
     */

    fun initAddressesRecycler(addressesRecycler: RecyclerView) {
        with(addressesRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = addressesResultAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun searchAddresses(query: String) {
        suggestSession.suggest(query, boundingBox, searchOptions, this)
    }

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
        addressesResultAdapter.submitList(addressList, "")
    }

    override fun onError(p0: Error) {}

    /**
     * DrivingRouteListener
     */

    override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
        for (route in p0) {
            distanceToRouteMap[initRouteLength(route.geometry.points)] = route
        }

        if (distanceToRouteMap.isNotEmpty()) {
            val key = distanceToRouteMap.minOf { it.key }
            val route = distanceToRouteMap[key]
            val polyline = route?.geometry

            val startPoint = route?.requestPoints?.get(0)?.point ?: Point(0.0, 0.0)
            val endPoint = route?.requestPoints?.get(1)?.point ?: Point(0.0, 0.0)
            viewModel.savePolyline(key, polyline, startPoint, endPoint)
        }
    }

    override fun onDrivingRoutesError(p0: Error) {}

    fun submitRequest(start: Point, end: Point) {
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

    private fun initRouteLength(routes: List<Point>): Double {
        var dis = 0.0

        for (i in 0 until routes.size - 1) {
            val it = routes[i]
            dis += Geo.distance(it, routes[i + 1])
        }
        return dis
    }

    /*************************/

    fun onStart(address: AddressUI?) {
        if (address == null) {
            if (detectGps()) {
                permissionsController.methodRequiresLocationsPermission(activity) {
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

    fun drawDeliveryZones(deliveryZoneUIList: List<DeliveryZoneUI>?) {
        if (deliveryZoneUIList == null) return

        deliveryZoneUIList.forEach { deliveryZoneUI ->
            val zone = mapView.map.mapObjects.addPolygon(
                Polygon(LinearRing(deliveryZoneUI.pointList), ArrayList())
            )
            zone.fillColor = Color.parseColor(deliveryZoneUI.color).getColorWithAlpha(0.4f)
            zone.strokeWidth = 0.0f
            zone.zIndex = 100.0f
        }
        viewModel.updateZones(true)
    }

    private fun detectGps(): Boolean {
        val manager = locationManager ?: return false
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

}