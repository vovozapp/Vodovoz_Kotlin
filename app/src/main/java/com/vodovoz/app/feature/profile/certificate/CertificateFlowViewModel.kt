package com.vodovoz.app.feature.profile.certificate

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.ActivateCertificateBundleMapper.mapToUI
import com.vodovoz.app.ui.model.custom.ActivateCertificateBundleUI
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
class CertificateFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager,
) : PagingContractViewModel<CertificateFlowViewModel.CertificateState, CertificateFlowViewModel.CertificateEvents>(
    CertificateState()
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

    private fun fetchCardInfo() {

        viewModelScope.launch {
            flow { emit(repository.fetchActivateCertificateInfo()) }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(activateCertificateBundleUI = data),
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

    fun activateCertificate(code: String) {

        val userId = accountManager.fetchAccountId() ?: return

        viewModelScope.launch {
            flow { emit(repository.activateCertificate(userId, code)) }
                .flowOn(Dispatchers.IO)
                .onEach { response ->

                    when (response) {
                        is ResponseEntity.Hide -> {
                            eventListener.emit(
                                CertificateEvents.ActivateResult(
                                    "Ошибка",
                                    "Неизвестная ошибка"
                                )
                            )
                        }

                        is ResponseEntity.Error -> {
                            eventListener.emit(
                                CertificateEvents.ActivateResult(
                                    "Ошибка",
                                    response.errorMessage
                                )
                            )
                        }

                        is ResponseEntity.Success -> {
                            eventListener.emit(
                                CertificateEvents.ActivateResult(
                                    "Успешно",
                                    response.data
                                )
                            )
                        }
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


    data class CertificateState(
        val activateCertificateBundleUI: ActivateCertificateBundleUI? = null,
    ) : State

    sealed class CertificateEvents : Event {
        data class ActivateResult(val title: String, val message: String) : CertificateEvents()
    }
}