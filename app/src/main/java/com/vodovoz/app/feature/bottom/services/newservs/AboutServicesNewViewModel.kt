package com.vodovoz.app.feature.bottom.services.newservs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.*
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.feature.bottom.services.detail.model.ServicesDetailParser.mapToUI
import com.vodovoz.app.feature.bottom.services.detail.model.ServicesDetailParser.parseServiceDetail
import com.vodovoz.app.feature.bottom.services.detail.model.ServiceDetailUI
import com.vodovoz.app.feature.bottom.services.newservs.model.AboutServicesNew
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutServicesNewViewModel @Inject constructor(
    private val repository: MainRepository,
    private val savedStateHandle: SavedStateHandle
) : PagingContractViewModel<AboutServicesNewViewModel.AboutServicesState, AboutServicesNewViewModel.AboutServicesEvents>(AboutServicesState()) {

    private val serviceId = savedStateHandle.get<String>("serviceId")

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
            flow { emit(repository.fetchAboutServicesNew("spisok")) }
                .catch {
                    debugLog { "fetch about services error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    uiStateListener.value = state.copy(
                        data = state.data.copy(
                            item = it
                        ),
                        loadingPage = false,
                        error = null
                    )
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun fetchServiceDetailsData() {
        val id = serviceId ?: return
        viewModelScope.launch {
            flow { emit(repository.fetchServicesNewDetails("details", id)) }
                .catch {
                    debugLog { "fetch about services details error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseServiceDetail()
                    uiStateListener.value =  if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        state.copy(
                            data = state.data.copy(
                                detailItem = data
                            ),
                            loadingPage = false,
                            error = null
                        )
                    } else {
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

    sealed class AboutServicesEvents : Event {

    }

    data class AboutServicesState(
        val item: AboutServicesNew? = null,
        val detailItem: ServiceDetailUI? = null
    ) : State
}