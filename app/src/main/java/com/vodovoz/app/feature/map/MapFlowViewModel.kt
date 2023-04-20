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
import com.vodovoz.app.feature.map.test.model.MapTestResponse
import com.vodovoz.app.mapper.AddressMapper.mapToUI
import com.vodovoz.app.mapper.DeliveryZonesBundleMapper.mapToUI
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.ui.model.custom.DeliveryZonesBundleUI
import com.vodovoz.app.util.extensions.debugLog
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

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
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                deliveryZonesBundleUI = data,
                                addressUI = addressUI,
                                centerPoints = data.deliveryZoneUIList.filter { it.color == "#16c60c" }.get(0).pointList
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

     fun getTwoPointsDistance(start: Point, end: Point) : Float {
        val locA = Location("locationA").apply {
            latitude = start.latitude
            longitude = start.longitude
        }

        val locB = Location("locationB").apply {
            latitude = end.latitude
            longitude = end.longitude
        }
        return locA.distanceTo(locB)
    }

    fun changeAddress() {
        uiStateListener.value = state.copy(data = state.data.copy(
            addressUI = null
        ))
    }

    fun sendTestMapResponse(
        latitude: String,
        longitude: String,
        length: String,
        date: String
    ) {
        viewModelScope.launch {
            delay(3000)
            val addr = state.data.addressUI?.fullAddress?.substringAfter("Россия, ") ?:""
            debugLog { "send test map response address $addr" }

            flow { emit(repository.fetchTestMapResponse(addr, latitude, longitude, length, date)) }
                .catch {
                    debugLog { "send test map response error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    eventListener.emit(MapFlowEvents.ShowAlert(it))
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun savePointData(
        latitude: String,
        longitude: String,
        length: String
    ) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                savedPointData = SavedPointData(latitude, longitude, length)
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
        val savedPointData: SavedPointData? = null
    ) : State

    sealed class MapFlowEvents : Event {

        data class ShowAddAddressBottomDialog(val address: AddressUI?) : MapFlowEvents()
        data class Submit(val startPoint: Point, val list: List<Point>): MapFlowEvents()
        data class ShowInfoDialog(val url: String?): MapFlowEvents()
        data class ShowAlert(val response: ResponseBody) : MapFlowEvents()
    }
}