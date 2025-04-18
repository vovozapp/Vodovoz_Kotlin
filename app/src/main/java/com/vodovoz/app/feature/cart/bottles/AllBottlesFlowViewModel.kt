package com.vodovoz.app.feature.cart.bottles

import androidx.compose.runtime.Immutable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.stringToErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.cart.bottles.model.BottleUi
import com.vodovoz.app.feature.cart.bottles.model.mapToUi
import com.vodovoz.app.mapper.BottleMapper.mapToUI
import com.vodovoz.app.ui.model.BottleUI
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllBottlesFlowViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val cartManager: CartManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<AllBottlesFlowViewModel.BottlesState, AllBottlesFlowViewModel.BottlesEvent>(
    BottlesState()
) {

    private val addBottleCompletedMLD = MutableLiveData(false)

    val addBottleCompletedLD: LiveData<Boolean> = addBottleCompletedMLD

    init {
        fetchAllBottlesDetails()
        listenBottlesChanges()
    }

    private fun listenBottlesChanges() = viewModelScope.launch {
        uiStateListener.map { pagingState -> pagingState.data.bottles }.collectLatest {
            uiStateListener.updateData { s ->
                s.copy(hideButton = !s.bottles.any { bottle -> bottle.cartQuantity > 0 } || s.isSingleBottleMode)
            }
        }
    }

    fun updateData() {
        viewModelScope.launch {

            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            flow { emit(mainRepository.fetchBottles()) }
                .onEach { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {}
                        is ResponseEntity.Error -> state.copy(
                            error = response.errorMessage.stringToErrorState(),
                            loadingPage = false
                        )

                        is ResponseEntity.Success -> {
                            val bottleList = response.data.mapToUI()
                            uiStateListener.value = state.copy(
                                loadingPage = false,
                                data = state.data.copy(itemsList = bottleList),
                                error = null
                            )

                        }
                    }
                }
                .collect()
        }
    }

    fun fetchAllBottlesDetails() = viewModelScope.launch(Dispatchers.IO) {
        uiStateListener.updateData { s ->
            s.copy(uiState = BottlesUiState.Loading)
        }

        val allBottlesResult = vodovozServiceRepository.getAllBottles().singleResult()

        allBottlesResult.onSuccess { allBottlesDetails ->
            uiStateListener.updateData { s ->
                s.copy(
                    description = allBottlesDetails.description,
                    isSingleBottleMode = allBottlesDetails.isSingleBottleMode,
                    uiState = BottlesUiState.Success,
                    bottles = allBottlesDetails.bottles.mapToUi()
                )
            }
        }.onFailure {
            uiStateListener.updateData { s ->
                s.copy(uiState = BottlesUiState.Error)
            }
        }
    }

    fun addBottleToCart(bottleId: Long) {
        viewModelScope.launch {
            runCatching { cartManager.add(bottleId, 0, 1) }
                .onSuccess { addBottleCompletedMLD.value = true }
                .onFailure {
                    uiStateListener.value = state.copy(
                        error = it.message?.stringToErrorState()
                            ?: "Неизвестная ошибка".stringToErrorState(),
                        loadingPage = false
                    )
                }
        }
    }

    fun changeSearchMode(searchMode: Boolean) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(isSearchMode = searchMode, searchQuery = "")
        }
    }

    fun changeSearchQuery(newQuery: String) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(searchQuery = newQuery)
        }
    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(BottlesEvent.GoBack)
    }

    fun addBottle(bottle: BottleUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                bottles = s.bottles.map { b -> if (b.id == bottle.id) b.copy(cartQuantity = 1) else b }
            )
        }


        if (dataState.isSingleBottleMode) {
            addBottlesToCart()
            return@launch
        }
    }

    fun incrementBottle(bottle: BottleUi) = viewModelScope.launch {
        if (dataState.isSingleBottleMode) return@launch

        uiStateListener.updateData { s ->
            s.copy(bottles = s.bottles.map { if (it.id == bottle.id) it.copy(cartQuantity = it.cartQuantity + 1) else it })
        }

    }

    fun decrementBottle(bottle: BottleUi) = viewModelScope.launch {
        if (bottle.cartQuantity <= 0 || dataState.isSingleBottleMode) return@launch

        uiStateListener.updateData { s ->
            s.copy(
                bottles = s.bottles.map {
                    if (it.id == bottle.id) it.copy(cartQuantity = it.cartQuantity - 1)
                    else it
                }
            )
        }
    }

    fun addBottlesToCart() = viewModelScope.launch {
        if (dataState.isSingleBottleMode) {
            uiStateListener.updateData { s ->
                s.copy(uiState = BottlesUiState.Loading)
            }
        } else {
            uiStateListener.updateData { s ->
                s.copy(
                    buttonIsLoading = true
                )
            }
        }


        val bottlesMap = dataState.bottles.associate { it.id to it.cartQuantity }
        val addBottlesResult = vodovozServiceRepository
            .addMultipleProductsToCart(cartManager.formatCart(bottlesMap))
            .singleResult()

        addBottlesResult.onSuccess {
            cartManager.updateCartListState(true)
            cartManager.observeUpdateCartList().collectLatest { hasUpdates ->
                if (!hasUpdates) {
                    uiStateListener.updateData { s ->
                        s.copy(buttonIsLoading = false)
                    }
                    eventListener.emit(BottlesEvent.GoBack)
                }
            }
        }.onFailure {
            uiStateListener.updateData { s ->
                s.copy(buttonIsLoading = false)
            }
            if (dataState.isSingleBottleMode) {
                eventListener.emit(BottlesEvent.GoBack)
            }
        }


    }


    @Immutable
    data class BottlesState(
        val itemsList: List<BottleUI> = emptyList(),

        val description: String = "",
        val bottles: List<BottleUi> = emptyList(),
        val isSingleBottleMode: Boolean = false,
        val uiState: BottlesUiState = BottlesUiState.Loading,
        val searchQuery: String = "",
        val isSearchMode: Boolean = false,
        val hideButton: Boolean = true,
        val buttonIsLoading: Boolean = false,
    ) : State

    sealed interface BottlesUiState {
        data object Loading : BottlesUiState
        data object Error : BottlesUiState
        data object Success : BottlesUiState
    }

    sealed interface BottlesEvent : Event {
        data object GoBack : BottlesEvent
    }
}