package com.vodovoz.app.feature.search.qrcode

import androidx.camera.core.Preview
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
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@HiltViewModel
class QrCodeViewModel @Inject constructor(
    private val repository: MainRepository,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<QrCodeViewModel.QrCodeState, QrCodeViewModel.QrCodeEvents>(QrCodeState()) {

    private val mutex = Mutex()

    fun searchByBarCode(barCode: String) = viewModelScope.launch {
        if (!mutex.tryLock()) return@launch

        uiStateListener.updateData { s ->
            s.copy(barCode = barCode)
        }

        val barCodeProductsResult =
            vodovozServiceRepository.getBarCodeProducts(barCode).singleResult()

        barCodeProductsResult.onSuccess { products ->
            uiStateListener.updateData { s ->
                s.copy(uiState = QrCodeUiState.Scanner)
            }

            val product = products.firstOrNull()
            if (products.size == 1 && product != null) {
                eventListener.emit(QrCodeEvents.GoToProductDetails(product.id))
            } else if (products.size > 1) {
                eventListener.emit(QrCodeEvents.GoToSearchProducts(barCode))
            }
        }.onFailure { t ->

            when (t) {
                is EmptyResultException -> {

                    val errorModel = t.data

                    uiStateListener.updateData { s ->
                        s.copy(
                            uiState = QrCodeUiState.EmptyResult(
                                errorModel.titleHtml,
                                errorModel.descriptionHtml,
                                errorModel.imageUrl
                            )
                        )
                    }
                }
            }
        }
    }.invokeOnCompletion { if(mutex.isLocked){ mutex.unlock() } }

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

    fun setScannerState() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(uiState = QrCodeUiState.Scanner)
        }
    }


    data class QrCodeState(
        val item: Item? = null,
        val flashOn: Boolean = false,
        val barCode: String = "",
        val uiState: QrCodeUiState = QrCodeUiState.Scanner,
    ) : State

    sealed interface QrCodeUiState {

        data object Scanner : QrCodeUiState
        data class EmptyResult(
            val title: String,
            val description: String,
            val imageUrl: String,
        ) : QrCodeUiState

    }

    sealed class QrCodeEvents : Event {
        data class Success(val id: String) : QrCodeEvents()
        data class GoToProductDetails(val id: Long) : QrCodeEvents()
        data class GoToSearchProducts(val barCode: String) : QrCodeEvents()
        data object GoBack : QrCodeEvents()
    }
}