package com.vodovoz.app.feature.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.*
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.map.AddAddressResponseJsonParser.parseAddAddressResponse
import com.vodovoz.app.data.parser.response.map.AddressByGeocodeResponseJsonParser.parseAddressByGeocodeResponse
import com.vodovoz.app.data.parser.response.map.UpdateAddressResponseJsonParser.parseUpdateAddressResponse
import com.vodovoz.app.feature.addresses.add.AddAddressFlowViewModel
import com.vodovoz.app.feature.map.manager.DeliveryZonesManager
import com.vodovoz.app.mapper.AddressMapper.mapToUI
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.ui.model.custom.DeliveryZonesBundleUI
import com.vodovoz.app.util.extensions.debugLog
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class MapFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val deliveryZonesManager: DeliveryZonesManager,
    private val accountManager: AccountManager
) : PagingContractViewModel<MapFlowViewModel.MapFlowState, MapFlowViewModel.MapFlowEvents>(
    MapFlowState(addressUI = savedState.get<AddressUI>("address"))
) {

    init {
        viewModelScope.launch {
            deliveryZonesManager
                .observeDeliveryZonesState()
                .collect { deliveryState ->
                    if (deliveryState == null) {
                        deliveryZonesManager.fetchDeliveryZonesBundle()
                    } else {
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                deliveryZonesBundleUI = deliveryState.deliveryZonesBundleUI
                            )
                        )
                    }
                }
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

    fun fetchSeveralMinimalLineDistancesToMainPolygonPoints(startPoint: Point, pendingUpdateAddressUI: AddressUI? = null) {
        viewModelScope.launch {
            val listOnPoints = deliveryZonesManager.fetchSeveralMinimalLineDistancesToMainPolygonPoints(startPoint)
            uiStateListener.value = state.copy(data = state.data.copy(pendingUpdateAddressUI = pendingUpdateAddressUI))
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
        val mappedAddress = state.data.addressUI?.copy(
            latitude = latitude,
            longitude = longitude,
            length = length
        )
        uiStateListener.value = state.copy(

            data = state.data.copy(
                savedPointData = SavedPointData(latitude, longitude, length),
                distance = distance,
                addressUI = mappedAddress
            )
        )
    }

    fun addPolyline(distance: Double, polyline: Polyline?, startPoint: Point, endPoint: Point) {
        viewModelScope.launch {

            if (polyline == null) {
                eventListener.emit(MapFlowEvents.ShowPolyline())
                return@launch
            }

            val newDistance = (distance / 1000).roundToInt().toString()

            if (deliveryZonesManager.containsInCenterPolygon(endPoint)) {
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

    fun action(
        entrance: String?,
        floor: String?,
        office: String?,
        comment: String?,
        type: Int?
    ) {
        val userId = accountManager.fetchAccountId() ?: return
        val addressId = state.data.addressUI?.id

        val locality = state.data.addressUI?.locality
        val street = state.data.addressUI?.street
        val house = state.data.addressUI?.house
        val lat = state.data.addressUI?.latitude
        val longitude = state.data.addressUI?.longitude
        val length = state.data.addressUI?.length
        val fullAddress = state.data.addressUI?.fullAddress?.substringAfter("Россия, ")

        if (house.isNullOrEmpty() || lat.isNullOrEmpty() || longitude.isNullOrEmpty() || length.isNullOrEmpty() || fullAddress.isNullOrEmpty()) {
            viewModelScope.launch {
                eventListener.emit(MapFlowEvents.ShowSearchError)
            }
            return
        }

        if (addressId == null || addressId == 0L) {
            addAddress(locality, street, house, entrance, floor, office, comment, type, userId, lat, longitude, length, fullAddress)
        } else {
            updateAddress(locality, street, house, entrance, floor, office, comment, type, userId, addressId, lat, longitude, length, fullAddress)
        }
    }

    private fun addAddress(
        locality: String?,
        street: String?,
        house: String?,
        entrance: String?,
        floor: String?,
        office: String?,
        comment: String?,
        type: Int?,
        userId: Long,
        lat: String,
        longitude: String,
        length: String,
        fullAddress: String
    ) {
        uiStateListener.value = state.copy(loadingPage = true)

        viewModelScope.launch {
            flow {
                emit(
                    repository.addAddress(
                        locality = locality,
                        street = street,
                        house = house,
                        entrance = entrance,
                        floor = floor,
                        office = office,
                        comment = comment,
                        type = type,
                        userId = userId,
                        lat = lat,
                        longitude = longitude,
                        length = length,
                        fullAddress = fullAddress
                    )
                )
            }
                .catch {
                    debugLog { "add address error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseAddAddressResponse()
                    uiStateListener.value = state.copy(
                        loadingPage = false,
                        error = null
                    )
                    when (response) {
                        is ResponseEntity.Success -> eventListener.emit(MapFlowEvents.AddAddressSuccess)
                        is ResponseEntity.Error -> eventListener.emit(MapFlowEvents.AddAddressError(response.errorMessage))
                        is ResponseEntity.Hide -> eventListener.emit(MapFlowEvents.AddAddressError("Неизвестная ошибка"))
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun updateAddress(
        locality: String?,
        street: String?,
        house: String?,
        entrance: String?,
        floor: String?,
        office: String?,
        comment: String?,
        type: Int?,
        userId: Long,
        addressId: Long,
        lat: String,
        longitude: String,
        length: String,
        fullAddress: String
    ) {
        uiStateListener.value = state.copy(loadingPage = true)

        viewModelScope.launch {
            flow {
                emit(
                    repository.updateAddress(
                        locality = locality,
                        street = street,
                        house = house,
                        entrance = entrance,
                        floor = floor,
                        office = office,
                        comment = comment,
                        type = type,
                        userId = userId,
                        addressId = addressId,
                        lat = lat,
                        longitude = longitude,
                        length = length,
                        fullAddress = fullAddress
                    )
                )
            }
                .catch {
                    debugLog { "update address error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseUpdateAddressResponse()
                    uiStateListener.value = state.copy(
                        loadingPage = false,
                        error = null
                    )
                    when (response) {
                        is ResponseEntity.Success -> eventListener.emit(MapFlowEvents.AddAddressSuccess)
                        is ResponseEntity.Error -> eventListener.emit(MapFlowEvents.AddAddressError(response.errorMessage))
                        is ResponseEntity.Hide -> eventListener.emit(MapFlowEvents.AddAddressError("Неизвестная ошибка"))
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    data class SavedPointData(
        val latitude: String,
        val longitude: String,
        val length: String
    )

    data class MapFlowState(
        val deliveryZonesBundleUI: DeliveryZonesBundleUI? = null,
        val addressUI: AddressUI? = null,
        val updateZones: Boolean = false,
        val savedPointData: SavedPointData? = null,
        val polyline: Polyline? = null,
        val distance: Double? = null,
        val pendingUpdateAddressUI: AddressUI? = null
    ) : State

    sealed class MapFlowEvents : Event {

        data class ShowAddAddressBottomDialog(val address: AddressUI?) : MapFlowEvents()
        data class Submit(val startPoint: Point, val list: List<Point>): MapFlowEvents()
        data class ShowInfoDialog(val url: String?): MapFlowEvents()
        data class ShowAlert(val response: ResponseBody) : MapFlowEvents()
        data class ShowPolyline(val polyline: Polyline? = null, val message: String? = null) : MapFlowEvents()
        object AddAddressSuccess : MapFlowEvents()
        data class AddAddressError(val message: String) : MapFlowEvents()
        object ShowSearchError : MapFlowEvents()
    }
}