package com.vodovoz.app.feature.preorder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.pre_order.PreOrderFormDataResponseJsonParser.parsePreOrderFormDataResponse
import com.vodovoz.app.data.parser.response.pre_order.PreOrderProductResponseJsonParser.parsePreOrderProductResponse
import com.vodovoz.app.mapper.PreOrderFormDataMapper.mapToUI
import com.vodovoz.app.ui.model.PreOrderFormDataUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreOrderFlowViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val dataRepository: DataRepository
) : PagingStateViewModel<PreOrderFlowViewModel.PreOrderState>(PreOrderState()) {

    private val productId = savedState.get<Long>("productId")

    private val preOrderSuccess = MutableSharedFlow<String>()
    fun observePreOrderSuccess() = preOrderSuccess.asSharedFlow()

    fun fetchPreOrderFormData() {
        val userId = dataRepository.fetchUserId() ?: return
        viewModelScope.launch {
            flow { emit(repository.fetchPreOrderFormData(userId)) }
                .catch {
                    debugLog { "fetch pre order form data error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parsePreOrderFormDataResponse()
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
                .collect()
        }
    }

    fun preOrderProduct(
        name: String,
        email: String,
        phone: String
    ) {
        val userId = dataRepository.fetchUserId() ?: return
        viewModelScope.launch {
            flow { emit(repository.preOrderProduct(userId, productId, name, email, phone)) }
                .catch { debugLog { "pre order product error ${it.localizedMessage}" } }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parsePreOrderProductResponse()
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
                .collect()
        }
    }

    data class PreOrderState(
        val items: PreOrderFormDataUI? = null
    ) : State
}