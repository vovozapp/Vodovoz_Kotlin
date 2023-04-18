package com.vodovoz.app.feature.bottom.services

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.*
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.service.AboutServicesResponseJsonParser.parseAboutServicesResponse
import com.vodovoz.app.data.parser.response.service.ServiceByIdResponseJsonParser.parseServiceByIdResponse
import com.vodovoz.app.feature.bottom.services.detail.bottom.adapter.ServiceNameItem
import com.vodovoz.app.mapper.AboutServicesBundleMapper.mapToUI
import com.vodovoz.app.mapper.ServiceMapper.mapToUI
import com.vodovoz.app.ui.model.ServiceUI
import com.vodovoz.app.ui.model.custom.AboutServicesBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutServicesFlowViewModel @Inject constructor(
    private val repository: MainRepository
) : PagingContractViewModel<AboutServicesFlowViewModel.AboutServicesState, AboutServicesFlowViewModel.AboutServicesEvents>(AboutServicesState()) {

    private val aboutServicesEventListener = MutableSharedFlow<AboutServicesEvents>(replay = 0, extraBufferCapacity = 1, BufferOverflow.DROP_OLDEST)
    fun observeAboutServicesEvents() = aboutServicesEventListener.asSharedFlow()

    fun firstLoadSorted() {
        if (!state.isFirstLoad) {
            uiStateListener.value =
                state.copy(isFirstLoad = true, loadingPage = true)
            fetchServicesData()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true)
        fetchServicesData()
    }

    private fun fetchServicesData() {
        viewModelScope.launch {
            flow { emit(repository.fetchAboutServices("glav")) }
                .catch {
                    debugLog { "fetch about services error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseAboutServicesResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()

                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                item = data
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

    fun navigateToDetails(type: String) {
        viewModelScope.launch {
            val typeList = state.data.item?.serviceUIList?.map { it.type } ?: return@launch
            uiStateListener.value = state.copy(
                data = state.data.copy(
                    selectedType = type
                )
            )
            aboutServicesEventListener.emit(AboutServicesEvents.NavigateToDetails(typeList, type))
        }
    }

    private fun fetchServiceByType(type: String) {
        uiStateListener.value = state.copy(loadingPage = true)

        viewModelScope.launch {
            flow { emit(repository.fetchAboutServices(type)) }
                .catch {
                    debugLog { "fetch service by type error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseServiceByIdResponse(type)
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()

                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                selectedService = data,
                                itemsWithFullText = state.data.itemsWithFullText + listOf(data),
                                selectedType = data.type
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

    fun selectService(type: String) {
        val service = state.data.itemsWithFullText.find { it.type == type }
        if (service == null) {
            fetchServiceByType(type)
        } else {
            val list = state.data.item?.serviceUIList
            if (list.isNullOrEmpty()) return

            val namedList = list.map {
                ServiceNameItem(
                    it.name,
                    it.type,
                    isSelected = it.type == type
                )
            }
            uiStateListener.value = state.copy(
                data = state.data.copy(
                    selectedType = type,
                    selectedService = service,
                    nameItemList = namedList
                )
            )
        }
    }

    fun onTitleClick() {
        viewModelScope.launch {
            val list = state.data.item?.serviceUIList
            if (list.isNullOrEmpty()) return@launch
            if (state.data.selectedType == null) return@launch

            val namedList = list.map {
                ServiceNameItem(
                    it.name,
                    it.type,
                    isSelected = it.type == state.data.selectedType
                )
            }

            uiStateListener.value = state.copy(
                data = state.data.copy(
                    nameItemList = namedList
                )
            )
            aboutServicesEventListener.emit(AboutServicesEvents.OnTitleClick(state.data.nameItemList))
        }
    }

    fun navigateToOrder() {
        val service: ServiceUI = state.data.selectedService ?: return
        viewModelScope.launch {
            aboutServicesEventListener.emit(AboutServicesEvents.NavigateToOrder(service.name, service.type))
        }
    }

    sealed class AboutServicesEvents : Event {
        data class NavigateToDetails(val typeList: List<String>, val type: String) : AboutServicesEvents()
        data class OnTitleClick(val nameItemList: List<ServiceNameItem>) : AboutServicesEvents()
        data class NavigateToOrder(val name: String, val type: String) : AboutServicesEvents()
    }

    data class AboutServicesState(
        val item: AboutServicesBundleUI? = null,
        val nameItemList: List<ServiceNameItem> = emptyList(),
        val selectedType: String? = null,
        val selectedService: ServiceUI? = null,
        val itemsWithFullText: List<ServiceUI> = emptyList()
    ) : State
}