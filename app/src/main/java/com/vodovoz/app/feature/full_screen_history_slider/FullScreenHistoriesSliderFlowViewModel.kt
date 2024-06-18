package com.vodovoz.app.feature.full_screen_history_slider

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.HistoryMapper.mapToUI
import com.vodovoz.app.ui.model.HistoryUI
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
//                    val response = it.parseHistoriesSliderResponse()
                    if (it is ResponseEntity.Success) {
                        it.data.mapToUI().let { data ->
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