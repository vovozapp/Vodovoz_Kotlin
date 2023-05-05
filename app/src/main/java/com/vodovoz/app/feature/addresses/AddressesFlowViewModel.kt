package com.vodovoz.app.feature.addresses

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.*
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.map.DeleteAddressResponseJsonParser.parseDeleteAddressResponse
import com.vodovoz.app.data.parser.response.map.FetchAddressesSavedResponseJsonParser.parseFetchAddressesSavedResponse
import com.vodovoz.app.mapper.AddressMapper.mapToUI
import com.vodovoz.app.feature.cart.ordering.OrderType
import com.vodovoz.app.ui.model.AddressFlowTitle
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressesFlowViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val application: Application
) : PagingContractViewModel<AddressesFlowViewModel.AddressesState, AddressesFlowViewModel.AddressesEvents>(AddressesState()){

    private val openMode = savedState.get<String>("openMode")
    private val addressType = savedState.get<String>("addressType")

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            fetchAddresses()
        }
    }

    fun refresh() {
        uiStateListener.value = state.copy(loadingPage = true)
        fetchAddresses()
    }

    private fun fetchAddresses() {
        val userId = accountManager.fetchAccountId() ?: return
        val type = when(addressType) {
            "RERSONAL" -> 1
            "COMPANY" -> 2
            else -> null
        }
        viewModelScope.launch {
            flow { emit(repository.fetchAddressesSaved(userId, type)) }
                .catch {
                    debugLog { "fetch addresses error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseFetchAddressesSavedResponse()

                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()

                        if (data.isNotEmpty()) {

                            val personal = data.filter { it.type == OrderType.PERSONAL.value }
                            val company = data.filter { it.type == OrderType.COMPANY.value }
                            val fullList = mutableListOf<Item>()
                            if (personal.isNotEmpty()) {
                                fullList.addAll(listOf(AddressFlowTitle(application.resources.getString(R.string.personal_addresses_title))) + personal)
                            }
                            if (company.isNotEmpty()) {
                                fullList.addAll(listOf(AddressFlowTitle(application.resources.getString(R.string.company_addresses_title))) + company)
                            }

                            uiStateListener.value = state.copy(
                                data = state.data.copy(
                                    items = data,
                                    companyItems = company,
                                    personalItems = personal,
                                    fullList = fullList
                                ),
                                loadingPage = false,
                                error = null
                            )
                        } else {
                            uiStateListener.value = state.copy(
                                error = ErrorState.Empty(),
                                loadingPage = false
                            )
                        }
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

    fun deleteAddress(addressId: Long) {
        val userId = accountManager.fetchAccountId() ?: return
        viewModelScope.launch {
            flow { emit(repository.deleteAddress(addressId = addressId, userId = userId)) }
                .catch {
                    debugLog { "fetch addresses error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {

                    when (val response = it.parseDeleteAddressResponse()) {
                        is ResponseEntity.Success -> {

                            val list = state.data.items.filter { it.id != addressId }
                            val personal = state.data.personalItems.filter { it.id != addressId }
                            val company = state.data.companyItems.filter { it.id != addressId }

                            val fullList = mutableListOf<Item>()
                            if (personal.isNotEmpty()) {
                                fullList.addAll(listOf(AddressFlowTitle(application.resources.getString(R.string.personal_addresses_title))) + personal)
                            }
                            if (company.isNotEmpty()) {
                                fullList.addAll(listOf(AddressFlowTitle(application.resources.getString(R.string.company_addresses_title))) + company)
                            }

                            uiStateListener.value = state.copy(
                                data = state.data.copy(
                                    items = list,
                                    companyItems = company,
                                    personalItems = personal,
                                    fullList = fullList
                                )
                            )
                            eventListener.emit(AddressesEvents.DeleteEvent("Удалено"))
                        }
                        is ResponseEntity.Error -> {
                            eventListener.emit(AddressesEvents.DeleteEvent(response.errorMessage))
                        }
                        is ResponseEntity.Hide -> {
                            eventListener.emit(AddressesEvents.DeleteEvent("Неизвестная ошибка"))
                        }
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun onAddressClick(address: AddressUI) {
        if (openMode != OpenMode.SelectAddress.name) return
        viewModelScope.launch {
            debugLog { "full ${address.fullAddress} length ${address.length} latitude ${address.latitude} longitude ${address.longitude}" }
            if (address.length.isNotEmpty()) {
                eventListener.emit(AddressesEvents.OnAddressClick(address))
            } else {
                eventListener.emit(AddressesEvents.UpdateAddress(address))
            }
        }
    }

    sealed class AddressesEvents : Event {
        data class DeleteEvent(val message: String) : AddressesEvents()
        data class OnAddressClick(val address: AddressUI) : AddressesEvents()
        data class UpdateAddress(val address: AddressUI) : AddressesEvents()
    }

    data class AddressesState(
        val items: List<AddressUI> = emptyList(),
        val companyItems: List<AddressUI> = emptyList(),
        val personalItems: List<AddressUI> = emptyList(),
        val fullList: List<Item> = emptyList()
    ) : State
}