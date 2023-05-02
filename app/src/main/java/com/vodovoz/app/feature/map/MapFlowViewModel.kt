package com.vodovoz.app.feature.map

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.*
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.map.AddressByGeocodeResponseJsonParser.parseAddressByGeocodeResponse
import com.vodovoz.app.data.parser.response.map.DeliveryZonesBundleResponseJsonParser.parseDeliveryZonesBundleResponse
import com.vodovoz.app.mapper.AddressMapper.mapToUI
import com.vodovoz.app.mapper.DeliveryZonesBundleMapper.mapToUI
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.ui.model.custom.DeliveryZonesBundleUI
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.polygoncreator.Polygon
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class MapFlowViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val dataRepository: DataRepository,
    private val repository: MainRepository
) : PagingContractViewModel<MapFlowViewModel.MapFlowState, MapFlowViewModel.MapFlowEvents>(
    MapFlowState()
) {

    private val addressUI = savedState.get<AddressUI>("address")

    private fun fetchDeliveryZonesBundle() {
        viewModelScope.launch {
            flow { emit(repository.fetchDeliveryZonesResponse()) }
                .catch {
                    debugLog { "fetch delivery zones error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseDeliveryZonesBundleResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        val centerP = data.deliveryZoneUIList.filter { it.color == "#16c60c" }.get(0).pointList
                        val moPoints = data.deliveryZoneUIList.filter { it.color == "#dfdddd" }.get(0).pointList
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                deliveryZonesBundleUI = data,
                                addressUI = addressUI,
                                centerPoints = centerP,
                                centerPolygon = buildCenterPolygon(centerP),
                                moPoints = moPoints,
                                moPolygon = buildCenterPolygon(moPoints)
                            ),
                            loadingPage = false,
                            error = null
                        )
                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error()
                            )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun updateZones(bool: Boolean) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                updateZones = bool
            )
        )
    }

    fun fetchAddressByGeocode(
        latitude: Double,
        longitude: Double
    ) {
        uiStateListener.value = state.copy(loadingPage = true)

        viewModelScope.launch {
            flow { emit(repository.fetchAddressByGeocodeResponse(latitude, longitude)) }
                .catch {
                    debugLog { "fetch address by geocode error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseAddressByGeocodeResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI().copy(id = state.data.addressUI?.id ?: 0)
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                addressUI = data
                            ),
                            error = null,
                            loadingPage = false
                        )

                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error()
                            )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun firstLoadSorted() {
        if (!state.isFirstLoad) {
            uiStateListener.value =
                state.copy(isFirstLoad = true, loadingPage = true)
            fetchDeliveryZonesBundle()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true)
        fetchDeliveryZonesBundle()
    }

    fun showAddAddressBottomDialog() {
        viewModelScope.launch {
            val mappedAddress = state.data.addressUI?.copy(
                latitude = state.data.savedPointData?.latitude ?: "",
                longitude = state.data.savedPointData?.longitude ?: "",
                length = state.data.savedPointData?.length ?: ""
            )
            eventListener.emit(MapFlowEvents.ShowAddAddressBottomDialog(mappedAddress))
        }
    }

    fun showInfoDialog() {
        viewModelScope.launch {
            eventListener.emit(MapFlowEvents.ShowInfoDialog(state.data.deliveryZonesBundleUI?.aboutDeliveryTimeUrl))
        }
    }

    fun fetchSeveralMinimalLineDistancesToMainPolygonPoints(startPoint: Point) {

        viewModelScope.launch {
            val moPolygon = state.data.moPolygon
            if (moPolygon != null) {
                if(!moPolygon.contains(com.vodovoz.app.util.polygoncreator.Point(startPoint.latitude, startPoint.longitude))) {
                    savePointData(
                        latitude = startPoint.latitude.toString(),
                        longitude = startPoint.longitude.toString(),
                        length = "0",
                        distance = 0.0
                    )
                    eventListener.emit(MapFlowEvents.ShowPolyline(message = "Вне зоны доставки"))
                }
            }
        }

        val sortedList = mutableListOf<LocationFloatToPoint>()

        state.data.centerPoints.forEach {

            val locA = Location("locationA").apply {
                    latitude = startPoint.latitude
                    longitude = startPoint.longitude
                }

            val locB = Location("locationB").apply {
                    latitude = it.latitude
                    longitude = it.longitude
                }

            val distance = locA.distanceTo(locB)
            sortedList.add(LocationFloatToPoint(distance, it))
        }

        val newList = sortedList.sortedBy { it.distance }
        val max = if (newList.size >= 5) {
            5
        } else {
            newList.size
        }
        val listOnPoints = newList.map { it.point }.subList(0,max)

        viewModelScope.launch {
            eventListener.emit(MapFlowEvents.Submit(startPoint, listOnPoints))
        }
    }

    fun changeAddress() {
        uiStateListener.value = state.copy(data = state.data.copy(
            addressUI = null
        ))
    }

    private fun savePointData(
        latitude: String,
        longitude: String,
        length: String,
        distance: Double
    ) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                savedPointData = SavedPointData(latitude, longitude, length),
                distance = distance
            )
        )
    }

    private fun buildCenterPolygon(centerPoints: List<Point>) : Polygon? {
        if (centerPoints.isEmpty()) return null

        return Polygon.Builder()
            .apply {
                centerPoints.forEach {
                    addVertex(com.vodovoz.app.util.polygoncreator.Point(it.latitude, it.longitude))
                }
            }
            .build()
    }

    fun addPolyline(distance: Double, polyline: Polyline?, startPoint: Point, endPoint: Point) {
        viewModelScope.launch {

            if (polyline == null) {
                eventListener.emit(MapFlowEvents.ShowPolyline())
                return@launch
            }

            val polygon = state.data.centerPolygon
            val centerPolygon = if (polygon != null) {
                polygon
            } else {
                eventListener.emit(MapFlowEvents.ShowPolyline())
                return@launch
            }
            val newDistance = (distance / 1000).roundToInt().toString()

            if (centerPolygon.contains(com.vodovoz.app.util.polygoncreator.Point(endPoint.latitude, endPoint.longitude))) {
                savePointData(
                    latitude = startPoint.latitude.toString(),
                    longitude = startPoint.longitude.toString(),
                    length = "0",
                    distance = 0.0
                )
                eventListener.emit(MapFlowEvents.ShowPolyline())
            } else {
                val length = state.data.distance
                if (length != null) {
                    if (distance < length) {
                        savePointData(
                            latitude = startPoint.latitude.toString(),
                            longitude = startPoint.longitude.toString(),
                            length = newDistance,
                            distance = distance
                        )
                        eventListener.emit(MapFlowEvents.ShowPolyline(polyline))
                    }
                } else {
                    savePointData(
                        latitude = startPoint.latitude.toString(),
                        longitude = startPoint.longitude.toString(),
                        length = newDistance,
                        distance = distance
                    )
                    eventListener.emit(MapFlowEvents.ShowPolyline(polyline))
                }
            }
        }

    }

    fun clear() {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                savedPointData = null,
                distance = null
            )
        )
    }

    data class SavedPointData(
        val latitude: String,
        val longitude: String,
        val length: String
    )

    data class LocationFloatToPoint(
        val distance: Float,
        val point: Point
    )

    data class MapFlowState(
        val deliveryZonesBundleUI: DeliveryZonesBundleUI? = null,
        val addressUI: AddressUI? = null,
        val updateZones: Boolean = false,
        val centerPoints: List<Point> = emptyList(),
        val centerPolygon: Polygon? = null,
        val savedPointData: SavedPointData? = null,
        val polyline: Polyline? = null,
        val distance: Double? = null,
        val moPoints: List<Point> = emptyList(),
        val moPolygon: Polygon? = null,
    ) : State

    sealed class MapFlowEvents : Event {

        data class ShowAddAddressBottomDialog(val address: AddressUI?) : MapFlowEvents()
        data class Submit(val startPoint: Point, val list: List<Point>): MapFlowEvents()
        data class ShowInfoDialog(val url: String?): MapFlowEvents()
        data class ShowAlert(val response: ResponseBody) : MapFlowEvents()
        data class ShowPolyline(val polyline: Polyline? = null, val message: String? = null) : MapFlowEvents()
    }
}