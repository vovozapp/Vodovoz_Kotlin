package com.vodovoz.app.feature.order_question_fragment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.R
import com.vodovoz.app.common.resources.ResourcesProvider
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.order_question_fragment.model.OrderQuestionEvent
import com.vodovoz.app.feature.order_question_fragment.model.OrderQuestionState
import com.vodovoz.app.feature.order_question_fragment.model.OrderQuestionUiState
import com.vodovoz.app.feature.preorder.model.EmptyTextValidator
import com.vodovoz.app.feature.preorder.model.FieldUi
import com.vodovoz.app.feature.preorder.model.PhoneNumberValidator
import com.vodovoz.app.feature.preorder.model.checkFields
import com.vodovoz.app.feature.preorder.model.getErrorText
import com.vodovoz.app.feature.preorder.model.mapToDomain
import com.vodovoz.app.feature.preorder.model.mapToUi
import com.vodovoz.app.feature.preorder.model.updateFieldAndResetErrors
import com.vodovoz.app.feature.preorder.model.vodovozValidators
import com.vodovoz.app.ui.mvi.MviViewModel
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderQuestionViewModel @Inject constructor(
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val resourcesProvider: ResourcesProvider,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<OrderQuestionState, OrderQuestionEvent>(
    OrderQuestionState()
) {
    private val orderId = savedStateHandle.get<Long>("orderId") ?: 0L.also { navigateBack() }

    init {
        viewModelScope.launch { delay(350) }.invokeOnCompletion {
            fetchOrderQuestionDetails()
        }
    }

    fun navigateBack() = viewModelScope.launch {
        _events.emit(OrderQuestionEvent.GoBack)
    }

    fun changeField(field: FieldUi, updatedField: FieldUi) = viewModelScope.launch {
        _state.update { s ->

            val updatedFields = s.fields.updateFieldAndResetErrors(field, updatedField)
            val buttonIsEnabled = updatedFields.checkFields(
                validators = listOf(PhoneNumberValidator, EmptyTextValidator)
            )

            s.copy(
                fields = updatedFields,
                button = s.button.copy(enabled = buttonIsEnabled)
            )
        }
    }

    fun sendMessage() = viewModelScope.launch {
        if (!stateSnapshot.button.enabled) return@launch

        val isValidFields = stateSnapshot.fields.checkFields(
            putErrors = true,
            validators = vodovozValidators,
            getSupportingText = { field ->
                field.getErrorText { id ->
                    resourcesProvider.getString(id)
                }
            }
        ) { fields, isValidFields ->
            _state.update { s ->
                s.copy(
                    fields = fields,
                    button = s.button.copy(
                        enabled = isValidFields
                    )
                )
            }
        }

        if (!isValidFields) return@launch

        _state.update { s ->
            s.copy(button = s.button.copy(loading = true))
        }

        val sendOrderQuestionResult =
            vodovozServiceRepository.sendOrderQuestion(orderId, stateSnapshot.fields.mapToDomain())
                .singleResult()

        sendOrderQuestionResult.onSuccess { placeholderData ->
            _state.update { s ->
                s.copy(
                    uiState = OrderQuestionUiState.Success(placeholderData.toUi()),
                    button = s.button.copy(loading = false)
                )
            }
        }.onFailure { t ->
            _events.emit(
                OrderQuestionEvent.ShowToast(
                    resourcesProvider.getString(R.string.order_question_send_error)
                )
            )
            _state.update { s ->
                s.copy(
                    button = s.button.copy(loading = false)
                )
            }
        }
    }

    fun fetchOrderQuestionDetails() = viewModelScope.launch {
        _state.update { s -> s.copy(uiState = OrderQuestionUiState.Loading) }

        val orderQuestionDetailsResult =
            vodovozServiceRepository.getOrderQuestionDetails(orderId).singleResult()
        orderQuestionDetailsResult.onSuccess { orderQuestionDetails ->

            _state.update { s ->
                s.copy(
                    title = orderQuestionDetails.title,
                    description = orderQuestionDetails.description,
                    fields = orderQuestionDetails.fields.mapToUi(),
                    uiState = OrderQuestionUiState.Fields,
                    button = orderQuestionDetails.button.toUi().copy(enabled = false)
                )
            }

        }.onFailure {
            _state.update { s ->
                s.copy(uiState = OrderQuestionUiState.Error)
            }
        }
    }

}