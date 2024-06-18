package com.vodovoz.app.feature.addresses

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.feature.cart.ordering.OrderType
import com.vodovoz.app.mapper.AddressMapper.mapToUI
import com.vodovoz.app.ui.model.AddressFlowTitle
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressesFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val application: Application,
) : PagingContractViewModel<AddressesFlowViewModel.AddressesState, AddressesFlowViewModel.AddressesEvents>(
    AddressesState()
) {

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
        val type = when (addressType) {
            OrderType.PERSONAL.name -> 1
            OrderType.COMPANY.name -> 2
            else -> null
        }
        viewModelScope.launch {
            flow { emit(repository.fetchAddressesSaved(userId, type)) }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()

                        if (data.isNotEmpty()) {
                            if(type == null) {
                                val personal = data.filter { it.type == OrderType.PERSONAL.value }
                                val company = data.filter { it.type == OrderType.COMPANY.value }
                                val fullList = mutableListOf<Item>()
                                if (personal.isNotEmpty()) {
                                    fullList.addAll(
                                        listOf(
                                            AddressFlowTitle(
                                                application.resources.getString(
                                                    R.string.personal_addresses_title
                                                )
                                            )
                                        ) + personal
                                    )
                                }
                                if (company.isNotEmpty()) {
                                    fullList.addAll(
                                        listOf(
                                            AddressFlowTitle(
                                                application.resources.getString(
                                                    R.string.company_addresses_title
                                                )
                                            )
                                        ) + company
                                    )
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
                                val addresses = if(type == OrderType.PERSONAL.value){
                                    data.filter { it.type == OrderType.PERSONAL.value }
                                } else {
                                    data.filter { it.type == OrderType.COMPANY.value }
                                }
                                uiStateListener.value = state.copy(
                                    data = state.data.copy(
                                        items = data,
                                        fullList = addresses
                                    ),
                                    loadingPage = false,
                                    error = null
                                )
                            }
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
                .catch {
                    debugLog { "fetch addresses error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun deleteAddress(addressId: Long) {
        val userId = accountManager.fetchAccountId() ?: return
        viewModelScope.launch {
            flow { emit(repository.deleteAddress(addressId = addressId, userId = userId)) }
                .flowOn(Dispatchers.IO)
                .onEach { response ->

                    when (response) {
                        is ResponseEntity.Success -> {

                            val list = state.data.items.filter { it.id != addressId }
                            val personal = state.data.personalItems.filter { it.id != addressId }
                            val company = state.data.companyItems.filter { it.id != addressId }

                            val fullList = mutableListOf<Item>()
                            if (personal.isNotEmpty()) {
                                fullList.addAll(
                                    listOf(
                                        AddressFlowTitle(
                                            application.resources.getString(
                                                R.string.personal_addresses_title
                                            )
                                        )
                                    ) + personal
                                )
                            }
                            if (company.isNotEmpty()) {
                                fullList.addAll(
                                    listOf(
                                        AddressFlowTitle(
                                            application.resources.getString(
                                                R.string.company_addresses_title
                                            )
                                        )
                                    ) + company
                                )
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
                .catch {
                    debugLog { "fetch addresses error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
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
        val fullList: List<Item> = emptyList(),
    ) : State
}