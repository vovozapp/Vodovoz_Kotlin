package com.vodovoz.app.feature.cart.bottles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.stringToErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.cart.BottlesResponseJsonParser.parseBottlesResponse
import com.vodovoz.app.mapper.BottleMapper.mapToUI
import com.vodovoz.app.ui.model.BottleUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllBottlesFlowViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val cartManager: CartManager,
) : PagingStateViewModel<AllBottlesFlowViewModel.BottlesState>(BottlesState()) {

    private val addBottleCompletedMLD = MutableLiveData(false)

    val addBottleCompletedLD: LiveData<Boolean> = addBottleCompletedMLD


    fun updateData() {
        viewModelScope.launch {

            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            flow { emit(mainRepository.fetchBottles()) }
                .flowOn(Dispatchers.IO)
                .onEach {
                    when (val response = it.parseBottlesResponse()) {
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

//    override fun onCleared() {
//        super.onCleared()
//        compositeDisposable.dispose()
//    }

    data class BottlesState(
        val itemsList: List<BottleUI> = emptyList(),
    ) : State
}