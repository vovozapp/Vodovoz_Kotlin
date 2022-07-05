package com.vodovoz.app.ui.components.fragment.map

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentMapBinding
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.model.DeliveryZoneUI
import com.vodovoz.app.util.Keys
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.runtime.image.ImageProvider


class MapDialogFragment : ViewStateBaseDialogFragment(), InputListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var viewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(Keys.MAPKIT_API_KEY)
        MapKitFactory.initialize(requireContext())
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[MapViewModel::class.java]
        viewModel.updateData()
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


    override fun update() {

    }

    override fun initView() {
        onStateSuccess()
        binding.mapView.map.addInputListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            Toast.makeText(requireContext(), "${state::class.simpleName}", Toast.LENGTH_SHORT).show()
//            when(state) {
//                is ViewState.Hide -> onStateHide()
//                is ViewState.Error -> onStateError(state.errorMessage)
//                is ViewState.Loading -> onStateLoading()
//                is ViewState.Success -> onStateLoading()
//            }
        }

        viewModel.deliveryZoneUIListLD.observe(viewLifecycleOwner) { deliveryZoneUIList ->
            drawDeliveryZones(deliveryZoneUIList)
        }
    }

    private fun drawDeliveryZones(deliveryZoneUIList: List<DeliveryZoneUI>) {
        deliveryZoneUIList.forEach { deliveryZoneUI ->
            val zone = binding.mapView.map.mapObjects.addPolygon(
                Polygon(LinearRing(deliveryZoneUI.pointList), ArrayList())
            )
            zone.fillColor = Color.parseColor(deliveryZoneUI.color)
            zone.strokeWidth = 0.0f
            zone.zIndex = 100.0f
        }
    }

    override fun onMapTap(p0: Map, p1: Point) {
        Toast.makeText(requireContext(), "CLICK", Toast.LENGTH_SHORT).show()
        binding.mapView.map.move(CameraPosition(p1, 16f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null)
        val placemark = binding.mapView.map.mapObjects.addPlacemark(
            p1,
            ImageProvider.fromResource(context, R.drawable.map_marker))
    }

    override fun onMapLongTap(p0: Map, p1: Point) {
        Toast.makeText(requireContext(), "LONG CLICK", Toast.LENGTH_SHORT).show()
    }


}