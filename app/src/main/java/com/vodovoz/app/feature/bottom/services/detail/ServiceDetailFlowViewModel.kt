package com.vodovoz.app.feature.bottom.services.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.*
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.service.AboutServicesResponseJsonParser.parseAboutServicesResponse
import com.vodovoz.app.data.parser.response.service.ServiceByIdResponseJsonParser.parseServiceByIdResponse
import com.vodovoz.app.feature.bottom.services.AboutServicesFlowViewModel
import com.vodovoz.app.mapper.AboutServicesBundleMapper.mapToUI
import com.vodovoz.app.mapper.ServiceMapper.mapToUI
import com.vodovoz.app.ui.model.ServiceUI
import com.vodovoz.app.ui.model.custom.AboutServicesBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceDetailFlowViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val repository: MainRepository
) : PagingContractViewModel<ServiceDetailFlowViewModel.ServiceDetailState, ServiceDetailFlowViewModel.ServiceDetailEvents>(
    ServiceDetailState()
) {

    private val typeList = savedState.get<Array<String>>("typeList")?.asList()
    private val type = savedState.get<String>("selectedType")

    fun fetchServices() {
        if (type == null) return
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
                                items = state.data.items + listOf(data)
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

    }

    fun onTitleClick() {
        viewModelScope.launch {
            if (typeList.isNullOrEmpty()) return@launch
            if (type.isNullOrEmpty()) return@launch
            eventListener.emit(ServiceDetailEvents.OnTitleClick(typeList, state.data.selectedService?.type ?: type))
        }
    }

    sealed class ServiceDetailEvents : Event {
        data class OnTitleClick(val typeList: List<String>, val selectedType: String) : ServiceDetailEvents()
    }

    data class ServiceDetailState(
        val items: List<ServiceUI> = emptyList(),
        val selectedService: ServiceUI? = null
    ) : State
}