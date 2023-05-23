package com.vodovoz.app.feature.search.qrcode

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrCodeViewModel @Inject constructor(
    private val repository: MainRepository,
) : PagingContractViewModel<QrCodeViewModel.QrCodeState, QrCodeViewModel.QrCodeEvents>(QrCodeState()) {

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
                    val id = list.get(0).id
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


    data class QrCodeState(
        val item: Item? = null,
    ) : State

    sealed class QrCodeEvents : Event {
        data class Success(val id: String) : QrCodeEvents()
        data class Error(val message: String) : QrCodeEvents()
    }
}