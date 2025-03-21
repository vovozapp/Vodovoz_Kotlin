package com.vodovoz.app.feature.search.qrcode

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.domain.general.model.EmptyResultException
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrCodeViewModel @Inject constructor(
    private val repository: MainRepository,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<QrCodeViewModel.QrCodeState, QrCodeViewModel.QrCodeEvents>(QrCodeState()) {

    fun searchByBarCode(barCode: String) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(uiState = QrCodeUiState.Scanner)
        }
        uiStateListener.updateData { s -> s.copy(barCode = barCode, uiState = QrCodeUiState.Scanner) }

        val barCodeProductsResult =
            vodovozServiceRepository.getBarCodeProducts(dataState.barCode).singleResult()

        barCodeProductsResult.onSuccess { products ->
            val product = products.firstOrNull()
            if (products.size == 1 && product != null) {
                eventListener.emit(QrCodeEvents.GoToProductDetails(product.id))
            } else if (products.size > 1) {
                eventListener.emit(QrCodeEvents.GoToSearchProducts(barCode))
            }
        }.onFailure { t ->

            when (t) {
                is EmptyResultException -> {
                    uiStateListener.updateData { s ->
                        s.copy(uiState = QrCodeUiState.Scanner)
                    }
                }

                else -> {
                    navigateBack()
                }
            }
        }
    }

    fun startSearchByQrCode(text: String?) {
        viewModelScope.launch {
            if (text.isNullOrEmpty()) {
                eventListener.emit(QrCodeEvents.Error("Ничего не найдено"))
                return@launch
            }

            runCatching { repository.fetchSearchDataByQrCode(text) }
                .onSuccess {
                    val list = it.listData
                    if (list.isNullOrEmpty()) {
                        eventListener.emit(QrCodeEvents.Error("Ничего не найдено"))
                        return@launch
                    }
                    val id = list[0].id
                    if (id.isNullOrEmpty()) {
                        eventListener.emit(QrCodeEvents.Error("Ничего не найдено"))
                        return@launch
                    }
                    eventListener.emit(QrCodeEvents.Success(id))
                }
                .onFailure {
                    eventListener.emit(QrCodeEvents.Error("Ничего не найдено"))
                }
        }
    }

    fun switchFlashOn() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                flashOn = !s.flashOn
            )
        }
    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(QrCodeEvents.GoBack)
    }


    data class QrCodeState(
        val item: Item? = null,
        val flashOn: Boolean = false,
        val barCode: String = "",
        val uiState: QrCodeUiState = QrCodeUiState.Scanner
    ) : State

    sealed interface QrCodeUiState {

        data object Scanner : QrCodeUiState
        data object EmptyResult : QrCodeUiState

    }

    sealed class QrCodeEvents : Event {
        data class Success(val id: String) : QrCodeEvents()
        data class Error(val message: String) : QrCodeEvents()
        data class GoToProductDetails(val id: Long) : QrCodeEvents()
        data class GoToSearchProducts(val barCode: String) : QrCodeEvents()
        data object GoBack : QrCodeEvents()
    }
}