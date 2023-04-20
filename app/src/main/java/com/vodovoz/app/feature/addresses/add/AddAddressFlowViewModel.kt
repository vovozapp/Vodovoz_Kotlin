package com.vodovoz.app.feature.addresses.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.contacts.SendMailResponseJsonParser.parseSendMailResponse
import com.vodovoz.app.data.parser.response.map.AddAddressResponseJsonParser.parseAddAddressResponse
import com.vodovoz.app.data.parser.response.map.UpdateAddressResponseJsonParser.parseUpdateAddressResponse
import com.vodovoz.app.feature.bottom.contacts.ContactsFlowViewModel
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAddressFlowViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager
) : PagingContractViewModel<AddAddressFlowViewModel.AddAddressState, AddAddressFlowViewModel.AddAddressEvents>(AddAddressState()){

    private val addressUi = savedState.get<AddressUI>("address")

    fun action(
        locality: String?,
        street: String?,
        house: String?,
        entrance: String?,
        floor: String?,
        office: String?,
        comment: String?,
        type: Int?,
        lat: String,
        longitude: String,
        length: String,
        fullAddress: String
    ) {
        val userId = accountManager.fetchAccountId() ?: return
        val addressId = addressUi?.id

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
                        is ResponseEntity.Success -> eventListener.emit(AddAddressEvents.AddAddressSuccess)
                        is ResponseEntity.Error -> eventListener.emit(AddAddressEvents.AddAddressError(response.errorMessage))
                        is ResponseEntity.Hide -> eventListener.emit(AddAddressEvents.AddAddressError("Неизвестная ошибка"))
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
                        is ResponseEntity.Success -> eventListener.emit(AddAddressEvents.AddAddressSuccess)
                        is ResponseEntity.Error -> eventListener.emit(AddAddressEvents.AddAddressError(response.errorMessage))
                        is ResponseEntity.Hide -> eventListener.emit(AddAddressEvents.AddAddressError("Неизвестная ошибка"))
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }


    sealed class AddAddressEvents : Event {
        object AddAddressSuccess : AddAddressEvents()
        data class AddAddressError(val message: String) : AddAddressEvents()
    }

    data class AddAddressState(
        val item: Item? = null
    ) : State
}