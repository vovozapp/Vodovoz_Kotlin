package com.vodovoz.app.feature.bottom.services.newservs

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.feature.bottom.services.newservs.model.AboutServicesNew
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutServicesNewViewModel @Inject constructor(
    private val repository: MainRepository
) : PagingContractViewModel<AboutServicesNewViewModel.AboutServicesState, AboutServicesNewViewModel.AboutServicesEvents>(AboutServicesState()) {

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

    private fun fetchServiceDetailsData(id: String?) {
        if (id == null) return
        viewModelScope.launch {
            flow { emit(repository.fetchServicesNewDetails("details", id)) }
                .catch {
                    debugLog { "fetch about services details error ${it.localizedMessage}" }
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

    sealed class AboutServicesEvents : Event {

    }

    data class AboutServicesState(
        val item: AboutServicesNew? = null,
    ) : State
}