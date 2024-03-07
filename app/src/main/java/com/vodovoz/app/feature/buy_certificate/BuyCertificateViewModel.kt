package com.vodovoz.app.feature.buy_certificate

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.BuyCertificateBundleMapper.mapToUI
import com.vodovoz.app.ui.model.custom.BuyCertificateBundleUI
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
class BuyCertificateViewModel @Inject constructor(
    private val repository: MainRepository,
) : PagingStateViewModel<BuyCertificateViewModel.BuyCertificateState>(BuyCertificateState()) {

    private val result: MutableMap<String, String> = mutableMapOf()

    fun getBuyCertificateBundle() {
        viewModelScope.launch {
            uiStateListener.value = state.copy(
                loadingPage = true
            )
            flow {
                emit(repository.fetchBuyCertificateInfo())
            }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        response.data.mapToUI().let { certificateBundle ->
                            uiStateListener.value = state.copy(
                                data = state.data.copy(
                                    buyCertificateBundleUI = certificateBundle
                                ),
                                loadingPage = false,
                                error = null
                            )
                            certificateBundle.buyCertificatePropertyUIList.forEach {
                                result[it.code] = ""
                            }
                        }
                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error()
                            )
                    }
                }.catch {
                    debugLog { "fetch certificate bundle error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun addResult(key: String, value: String) {
        result[key] = value
    }

    data class BuyCertificateState(
        val buyCertificateBundleUI: BuyCertificateBundleUI? = null,
    ) : State
}