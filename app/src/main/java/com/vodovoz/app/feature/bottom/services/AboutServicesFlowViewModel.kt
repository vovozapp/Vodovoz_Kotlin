package com.vodovoz.app.feature.bottom.services

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.*
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.service.AboutServicesResponseJsonParser.parseAboutServicesResponse
import com.vodovoz.app.mapper.AboutServicesBundleMapper.mapToUI
import com.vodovoz.app.ui.model.custom.AboutServicesBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutServicesFlowViewModel @Inject constructor(
    private val repository: MainRepository
) : PagingContractViewModel<AboutServicesFlowViewModel.AboutServicesState, AboutServicesFlowViewModel.AboutServicesEvents>(AboutServicesState()) {

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
            flow { emit(repository.fetchAboutServices()) }
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
            eventListener.emit(AboutServicesEvents.NavigateToDetails(typeList, type))
        }
    }

    sealed class AboutServicesEvents : Event {
        data class NavigateToDetails(val typeList: List<String>, val type: String) : AboutServicesEvents()
    }

    data class AboutServicesState(
        val item: AboutServicesBundleUI? = null
    ) : State
}