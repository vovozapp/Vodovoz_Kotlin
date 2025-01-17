package com.vodovoz.app.feature.service_order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.ServiceOrderFormFieldMapper.mapToUI
import com.vodovoz.app.ui.model.ServiceOrderFormFieldUI
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
class ServiceOrderViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    savedStateHandle: SavedStateHandle,
) : PagingStateViewModel<ServiceOrderViewModel.ServiceOrderState>(ServiceOrderState()) {

    private val serviceType = savedStateHandle.get<String>("serviceType")

    fun fetchData() {

        viewModelScope.launch {
            uiStateListener.value =
                state.copy(isFirstLoad = true, loadingPage = true, data = ServiceOrderState())
            val userId = accountManager.fetchAccountId()
            if(userId == null) {
                uiStateListener.value =
                    state.copy(
                        error = ErrorState.Error(
                            messageInfo = "Авторизуйтесь, пожалуйста",
                            desc = ""
                        ),
                        loadingPage = false
                    )
                return@launch
            }
            flow {
                emit(
                    repository.fetchFormForOrderService(
                        type = serviceType,
                        userId = userId
                    )
                )
            }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        response.data.mapToUI().let { data ->
                            uiStateListener.value = state.copy(
                                data = state.data.copy(
                                    serviceOrderFormFieldUIListMLD = data
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
                    debugLog { "fetch Form For Order error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun orderService(value: String) {

        viewModelScope.launch {
            uiStateListener.value =
                state.copy(isFirstLoad = true, loadingPage = true, data = ServiceOrderState())
            val userId = accountManager.fetchAccountId() ?: return@launch
            flow {
                emit(
                    repository.orderService(
                        type = serviceType,
                        userId = userId,
                        value = value
                    )
                )
            }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                successMessageMLD = response.data
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
                .catch {
                    debugLog { "fetch order Service error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    data class ServiceOrderState(
        val serviceOrderFormFieldUIListMLD: List<ServiceOrderFormFieldUI> = listOf(),
        val successMessageMLD: String = "",
    ) : State

}