package com.vodovoz.app.feature.full_screen_history_slider

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.*
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.history.HistoriesSliderResponseJsonParser.parseHistoriesSliderResponse
import com.vodovoz.app.mapper.HistoryMapper.mapToUI
import com.vodovoz.app.ui.model.HistoryUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullScreenHistoriesSliderFlowViewModel @Inject constructor(
    private val repository: MainRepository,
) : PagingContractViewModel<FullScreenHistoriesSliderFlowViewModel.HistoriesSliderState, FullScreenHistoriesSliderFlowViewModel.HistoriesSliderEvents>(
    HistoriesSliderState()
) {

    fun updateData() {
        viewModelScope.launch {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            flow {
                emit(
                    repository.fetchHistoriesSlider()
                )
            }.flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseHistoriesSliderResponse()
                    if (response is ResponseEntity.Success) {
                        response.data.mapToUI().let { data ->
                            uiStateListener.value = state.copy(
                                data = state.data.copy(
                                    historyUIList = data
                                ),
                                loadingPage = false,
                                error = null
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
                .catch {
                    debugLog { "fetch histories error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun goToProfile() {
        viewModelScope.launch {
            eventListener.emit(HistoriesSliderEvents.GoToProfile)
        }
    }

    sealed class HistoriesSliderEvents : Event {
        object GoToProfile : HistoriesSliderEvents()
    }

    data class HistoriesSliderState(
        val historyUIList: List<HistoryUI> = listOf(),
    ) : State
}