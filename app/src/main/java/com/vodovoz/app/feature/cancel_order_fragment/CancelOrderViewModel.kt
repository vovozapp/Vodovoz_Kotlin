package com.vodovoz.app.feature.cancel_order_fragment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.cancel_order_fragment.model.CancelOrderEvent
import com.vodovoz.app.feature.cancel_order_fragment.model.CancelOrderState
import com.vodovoz.app.feature.cancel_order_fragment.model.CancelOrderUiState
import com.vodovoz.app.ui.mvi.MviViewModel
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CancelOrderViewModel @Inject constructor(
    private val vodovozServiceRepository: VodovozServiceRepository,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<CancelOrderState, CancelOrderEvent>(CancelOrderState()) {

    private val orderId = savedStateHandle.get<Long>("orderId") ?: 0L.also { navigateBack() }

    init {
        viewModelScope.launch { delay(350L) }.invokeOnCompletion {
            fetchCancelOrderDetails()
        }
    }

    fun navigateBack() = viewModelScope.launch {
        _events.emit(CancelOrderEvent.GoBack)
    }

    fun fetchCancelOrderDetails() = viewModelScope.launch {
        _state.update { s ->
            s.copy(uiState = CancelOrderUiState.Loading)
        }

        val cancelOrderDetailsResult =
            vodovozServiceRepository.getCancelOrderDetails(orderId).singleResult()

        cancelOrderDetailsResult.onSuccess { cancelOrderDetails ->
            val checkboxes = cancelOrderDetails.checkboxesNames

            _state.update { s ->
                s.copy(
                    uiState = CancelOrderUiState.Body,
                    title = cancelOrderDetails.title,
                    warningText = cancelOrderDetails.warningText,
                    description = cancelOrderDetails.description,
                    checkboxesNames = checkboxes,
                    currentCheckboxName = checkboxes.firstOrNull() ?: "",
                    button = cancelOrderDetails.button.toUi()
                )
            }

        }.onFailure {
            _state.update { s ->
                s.copy(uiState = CancelOrderUiState.Error)
            }
            navigateBack()
        }
    }

    fun changeCurrentCheckbox(name: String) = viewModelScope.launch {
        _state.update { s ->
            s.copy(currentCheckboxName = name)
        }
    }

}