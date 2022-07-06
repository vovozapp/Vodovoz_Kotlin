package com.vodovoz.app.ui.components.fragment.map

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentMapBinding
import com.vodovoz.app.ui.components.adapter.AddressesResultAdapter
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.diffUtils.AddressDiffUtilCallback
import com.vodovoz.app.ui.extensions.ColorExtensions.getColorWithAlpha
import com.vodovoz.app.ui.model.DeliveryZoneUI
import com.vodovoz.app.util.Keys
import com.vodovoz.app.util.LogSettings
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
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.search.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class MapDialogFragment : ViewStateBaseFragment(),
    InputListener,
    UserLocationObjectListener,
    SuggestSession.SuggestListener
{

    private lateinit var binding: FragmentMapBinding
    private lateinit var viewModel: MapViewModel

    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var mapKit: MapKit

    private lateinit var searchManager: SearchManager
    private lateinit var suggestSession: SuggestSession
    private var lastPlaceMark: PlacemarkMapObject? = null
    private var isShowSearchResult = false

    private val center = Point(55.75, 37.62)
    private val boxSize = 0.2
    private val boundingBox = BoundingBox(
        Point(center.latitude - boxSize, center.longitude - boxSize),
        Point(center.latitude + boxSize, center.longitude + boxSize))
    private val searchOptions = SuggestOptions().setSuggestTypes(
        SuggestType.GEO.value or
        SuggestType.BIZ.value or
        SuggestType.TRANSIT.value
    )

    private val compositeDisposable = CompositeDisposable()
    private val onAddressClickSubject: PublishSubject<Pair<String, Point>> = PublishSubject.create()
    private val addressesAdapter = AddressesResultAdapter(onAddressClickSubject = onAddressClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(Keys.MAPKIT_API_KEY)
        MapKitFactory.initialize(requireContext())
        //setStyle(STYLE_NORMAL, R.style.MapDialog)
        mapKit = MapKitFactory.getInstance()

        initViewModel()
        subscribeSubjects()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[MapViewModel::class.java]
        viewModel.updateData()
    }

    private fun subscribeSubjects() {
        onAddressClickSubject.subscribeBy { pair ->
            binding.searchEdit.setText(pair.first)
            viewModel.fetchAddressByGeocode(
                pair.second.longitude,
                pair.second.longitude
            )
            moveCamera(pair.second)
            placeMark(pair.second, R.drawable.map_marker)
            hideSearchResultRecyclerView()
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup,
    ) = FragmentMapBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root


    override fun update() { viewModel.updateData() }

    override fun initView() {
        initMap()
        initSearch()
        initAddressesRecycler()
    }

    private fun initAddressesRecycler() {
        binding.addressesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.addressesRecycler.adapter = addressesAdapter
        binding.addressesRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }

    private fun initMap() {
        binding.mapView.map.addInputListener(this)
        userLocationLayer = mapKit.createUserLocationLayer(binding.mapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.setObjectListener(this)

        mapKit.createLocationManager().requestSingleUpdate(object : LocationListener {
            override fun onLocationUpdated(p0: Location) {
                Toast.makeText(requireContext(), "${p0.getPosition().latitude} ${p0.getPosition().longitude}", Toast.LENGTH_SHORT).show()
            }

            override fun onLocationStatusUpdated(p0: LocationStatus) {
                Toast.makeText(requireContext(), "UPDATE", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun initSearch() {
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        suggestSession = searchManager.createSuggestSession()

        binding.addAddress.setOnClickListener {
            findNavController().navigate(MapDialogFragmentDirections.actionToAddAddressBottomFragment(
                viewModel.addressUI
            ))
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

        binding.searchContainer.setOnClickListener { showSearchResultRecyclerView() }
        binding.searchEdit.setOnFocusChangeListener { _, isFocus ->
            Toast.makeText(requireContext(), isFocus.toString(), Toast.LENGTH_SHORT).show()
            if (isFocus) showSearchResultRecyclerView()
        }
        binding.searchImage.setOnClickListener {
            sequenceOf(Toast.makeText(requireContext(), isShowSearchResult.toString(), Toast.LENGTH_SHORT))
            if (isShowSearchResult) {
                binding.searchEdit.text = null
                hideSearchResultRecyclerView()
            }
            else {
                showSearchResultRecyclerView()
            }
        }
    }
    
    private fun showSearchResultRecyclerView() {
        isShowSearchResult = true
        binding.appBarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.addAddress.visibility = View.INVISIBLE
        binding.addressesRecycler.visibility = View.VISIBLE
        binding.searchImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_left))
        binding.searchEdit.focusSearch(View.FOCUS_UP)
        binding.searchContainer.elevation = 0f
        binding.searchContainer.setBackgroundResource(R.drawable.background_search_stroke_address)
    }

    private fun searchAddresses(query: String) {
        suggestSession.suggest(query, boundingBox, searchOptions, this)
    }

    private fun hideSearchResultRecyclerView() {
        isShowSearchResult = false
        binding.appBarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
        binding.addAddress.visibility = View.VISIBLE
        binding.addressesRecycler.visibility = View.INVISIBLE
        binding.searchImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.map_marker))
        binding.searchEdit.clearFocus()
        binding.searchContainer.elevation = resources.getDimension(R.dimen.card_elevation)
        binding.searchContainer.setBackgroundResource(R.drawable.background_search_address)
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            Toast.makeText(requireContext(), "${state::class.simpleName}", Toast.LENGTH_SHORT).show()
            when(state) {
                is ViewState.Hide -> onStateHide()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Loading -> onStateLoading()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.deliveryZoneUIListLD.observe(viewLifecycleOwner) { deliveryZoneUIList ->
            drawDeliveryZones(deliveryZoneUIList)
        }

        viewModel.addressUILD.observe(viewLifecycleOwner) { address ->
            binding.searchEdit.setText("${address.locality}, ${address.street}, ${address.house}")
        }
    }

    private fun drawDeliveryZones(deliveryZoneUIList: List<DeliveryZoneUI>) {
        deliveryZoneUIList.forEach { deliveryZoneUI ->
            val zone = binding.mapView.map.mapObjects.addPolygon(
                Polygon(LinearRing(deliveryZoneUI.pointList), ArrayList())
            )
            zone.fillColor = Color.parseColor(deliveryZoneUI.color).getColorWithAlpha(0.4f)
            zone.strokeWidth = 0.0f
            zone.zIndex = 100.0f
        }
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

    override fun onMapTap(map: Map, point: Point) {
        moveCamera(point)
        placeMark(point, R.drawable.map_marker)
        viewModel.fetchAddressByGeocode(
            point.latitude,
            point.longitude
        )
    }

    override fun onMapLongTap(map: Map, point: Point) {
        moveCamera(point)
        placeMark(point, R.drawable.map_marker)
        viewModel.fetchAddressByGeocode(
            point.latitude,
            point.longitude
        )
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        binding.mapView.map.move(CameraPosition(Point(
            userLocationView.arrow.geometry.latitude,
            userLocationView.arrow.geometry.longitude
        ), 16f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null)
    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }

    private val suggestResult: MutableList<String> = mutableListOf()

    override fun onResponse(itemList: MutableList<SuggestItem>) {
        suggestResult.clear()
        val addressList = mutableListOf<Pair<String, Point>>()
        for (item in itemList) {
            if (item.uri?.contains("geo") == true) {
                val coordinates = item.uri?.split("&")?.first()?.split("=")?.get(1)?.split("%2C")
                addressList.add(Pair(
                    item.displayText.toString(),
                    Point(
                        coordinates?.last()?.toDouble() ?: 0.0,
                        coordinates?.first()?.toDouble() ?: 0.0
                    )
                ))
            }
        }
        updateAddressesRecycler(addressList)
    }

    private fun updateAddressesRecycler(addressList: List<Pair<String, Point>>) {
        val diffUtil = AddressDiffUtilCallback(
            oldList = addressesAdapter.addressList,
            newList = addressList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            addressesAdapter.addressList = addressList
            diffResult.dispatchUpdatesTo(addressesAdapter)
        }
    }

    override fun onError(p0: Error) {
        Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_SHORT).show()
    }


}