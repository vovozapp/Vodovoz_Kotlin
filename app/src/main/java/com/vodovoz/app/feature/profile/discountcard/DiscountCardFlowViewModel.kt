package com.vodovoz.app.feature.profile.discountcard

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.ActivateDiscountCardBundleMapper.mapToUI
import com.vodovoz.app.ui.model.custom.ActivateDiscountCardBundleUI
import com.vodovoz.app.util.FieldValidationsSettings
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
class DiscountCardFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager,
) : PagingContractViewModel<DiscountCardFlowViewModel.DiscountCardState, DiscountCardFlowViewModel.DiscountCardEvents>(
    DiscountCardState()
) {

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            fetchCardInfo()
        }
    }

    fun refresh() {
        uiStateListener.value = state.copy(loadingPage = true)
        fetchCardInfo()
    }

    fun changeCardValue(value: String) {
        state.data.activateDiscountCardBundleUI?.discountCardPropertyUIList?.find { it.code == "TELEFON" }?.value =
            value
    }


    private fun fetchCardInfo() {
        val userId = accountManager.fetchAccountId() ?: return

        viewModelScope.launch {
            flow { emit(repository.fetchActivateDiscountCardInfo(userId)) }
                .onEach { response ->
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(activateDiscountCardBundleUI = data),
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
                .catch {
                    debugLog { "fetch card info error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(),
                            loadingPage = false
                        )
                }
                .collect()
        }
    }

    fun activateDiscountCard() {
        val valueBuilder = StringBuilder()
        var isCorrectly = true
        val bundle = state.data.activateDiscountCardBundleUI ?: return
        for (property in bundle.discountCardPropertyUIList) {
            when (property.code) {
                "TELEFON" -> if (!FieldValidationsSettings.PHONE_REGEX.matches(property.value)) {
                    debugLog { "property.value ${property.value}" }
                    isCorrectly = false
                    property.isValid = false
                }
                else -> if (property.value.isEmpty()) {
                    isCorrectly = false
                    property.isValid = false
                }
            }
            valueBuilder
                .append(property.code)
                .append("$")
                .append(property.value)
                .append(";")
        }

        if (!isCorrectly) {
            viewModelScope.launch {
                eventListener.emit(DiscountCardEvents.ActivateResult("Некорректные данные"))
            }
            return
        }

        val userId = accountManager.fetchAccountId() ?: return

        viewModelScope.launch {
            flow { emit(repository.activateDiscountCard(userId, valueBuilder.toString())) }
                .onEach { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            eventListener.emit(DiscountCardEvents.ActivateResult("Неизвестная ошибка"))
                        }
                        is ResponseEntity.Error -> {
                            eventListener.emit(DiscountCardEvents.ActivateResult(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            eventListener.emit(DiscountCardEvents.ActivateResult(response.data))
                        }
                        else -> {}
                    }

                    uiStateListener.value = state.copy(loadingPage = false)
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "activate card error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(),
                            loadingPage = false
                        )
                }
                .collect()
        }

    }


    data class DiscountCardState(
        val activateDiscountCardBundleUI: ActivateDiscountCardBundleUI? = null,
    ) : State

    sealed class DiscountCardEvents : Event {
        data class ActivateResult(val message: String) : DiscountCardEvents()
    }
}