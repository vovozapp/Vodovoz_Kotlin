package com.vodovoz.app.feature.preorder

import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.common.resources.ResourcesProvider
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.domain.general.model.ValidationException
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.preorder.model.FieldUi
import com.vodovoz.app.feature.preorder.model.PreOrderSectionUi
import com.vodovoz.app.feature.preorder.model.toDomain
import com.vodovoz.app.feature.preorder.model.toUi
import com.vodovoz.app.mapper.PreOrderFormDataMapper.mapToUI
import com.vodovoz.app.ui.model.PreOrderFormDataUI
import com.vodovoz.app.util.FieldValidationsSettings.EMAIL_REGEX
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.isValidRussianPhoneNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreOrderFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val resourcesProvider: ResourcesProvider,
) : PagingContractViewModel<PreOrderFlowViewModel.PreOrderState, PreOrderFlowViewModel.PreOrderEvent>(
    PreOrderState()
) {

    private val productId = savedState.get<Long>("productId") ?: -1L

    private val preOrderSuccess = MutableSharedFlow<String>()
    fun observePreOrderSuccess() = preOrderSuccess.asSharedFlow()

    fun fetchPreOrderData() = viewModelScope.launch {
        vodovozServiceRepository.getPreorderFields(productId)
            .onStart { uiStateListener.updateData { s -> s.copy(uiState = UiState.Loading) } }
            .onEach { preOrderSectionResult ->
                preOrderSectionResult.onSuccess { preOrderSectionModel ->
                    uiStateListener.updateData { s ->
                        s.copy(
                            uiState = UiState.Success,
                            sectionPreOrder = preOrderSectionModel.toUi(),
                        )
                    }
                }.onFailure {
                    uiStateListener.updateData { s ->
                        s.copy(uiState = UiState.Error)
                    }
                }
            }.collect()
    }

    fun sendPreOrder() = viewModelScope.launch {
        if (validateFields()) {
            val fields = dataState.sectionPreOrder.fields.map { field -> field.toDomain() }
            vodovozServiceRepository.sendPreorder(productId, fields).take(1).collect { result ->
                result.onSuccess { message ->
                    eventListener.emit(PreOrderEvent.ShowSnackbar(message, true))
                    eventListener.emit(PreOrderEvent.GoBack)
                }.onFailure { t ->
                    val errorMessage = when (t) {
                        is ValidationException -> {
                            t.message
                                ?: resourcesProvider.getString(R.string.error_message_send_failed)
                        }

                        else -> {
                            resourcesProvider.getString(R.string.error_message_send_failed)
                        }
                    }
                    eventListener.emit(PreOrderEvent.ShowSnackbar(errorMessage))
                }
            }
        }
        eventListener.emit(PreOrderEvent.HideKeyboard)
    }



    fun fetchPreOrderFormData() {
        val userId = accountManager.fetchAccountId() ?: return
        viewModelScope.launch {
            flow { emit(repository.fetchPreOrderFormData(userId)) }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = state.copy(
                            state.data.copy(items = data),
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
                .catch {
                    debugLog { "fetch pre order form data error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun preOrderProduct(
        name: String,
        email: String,
        phone: String,
    ) {
        val userId = accountManager.fetchAccountId() ?: return
        viewModelScope.launch {
            flow { emit(repository.preOrderProduct(userId, productId, name, email, phone)) }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            error = null
                        )
                        preOrderSuccess.emit(response.data)
                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error()
                            )
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch { debugLog { "pre order product error ${it.localizedMessage}" } }
                .collect()
        }
    }


    private fun validateFields(): Boolean {
        var isCorrectFields = true

        uiStateListener.updateData { s ->
            val fields = s.sectionPreOrder.fields
            s.copy(
                sectionPreOrder = s.sectionPreOrder.copy(
                    fields = fields.map { field ->
                        if (!field.isRequired) return@map field

                        return@map when {
                            field.keyboardType == KeyboardType.Phone && !field.value.isValidRussianPhoneNumber() -> {
                                isCorrectFields = false
                                field.copy(isError = true)
                            }

                            field.keyboardType == KeyboardType.Email && !EMAIL_REGEX.matches(field.value) -> {
                                isCorrectFields = false
                                field.copy(isError = true)
                            }

                            field.keyboardType == KeyboardType.Text && field.value.replace(
                                " ",
                                ""
                            ).length !in 2..1200 -> {
                                isCorrectFields = false
                                field.copy(isError = true)
                            }

                            field.keyboardType == KeyboardType.Number && field.value.toIntOrNull() == null -> {
                                isCorrectFields = false
                                field.copy(isError = true)
                            }

                            else -> field
                        }
                    }
                )
            )
        }
        return isCorrectFields
    }


    fun changeFieldValue(field: FieldUi, newValue: String) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            val sectionPreOrder = s.sectionPreOrder
            val fields = sectionPreOrder.fields
            val fieldIndex = fields.indexOfFirst { field.id == it.id }

            s.copy(
                sectionPreOrder = sectionPreOrder.copy(
                    fields = fields.toMutableList()
                        .apply { set(fieldIndex, field.copy(value = newValue)) }
                        .map { it.copy(isError = false) }
                )
            )
        }
    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(PreOrderEvent.GoBack)
    }

    data class PreOrderState(
        val items: PreOrderFormDataUI? = null,

        val sectionPreOrder: PreOrderSectionUi = PreOrderSectionUi.Empty,
        val uiState: UiState = UiState.Loading,
    ) : State

    sealed class PreOrderEvent : Event {
        data class ShowSnackbar(val message: String, val isVeryShort: Boolean = false) :
            PreOrderEvent()

        data object GoBack : PreOrderEvent()
        data object HideKeyboard : PreOrderEvent()
    }

    sealed interface UiState {
        data object Error : UiState
        data object Success : UiState
        data object Loading : UiState
    }
}