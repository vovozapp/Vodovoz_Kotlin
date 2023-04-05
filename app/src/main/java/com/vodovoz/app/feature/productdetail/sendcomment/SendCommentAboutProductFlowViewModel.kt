package com.vodovoz.app.feature.productdetail.sendcomment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.comment.SendCommentAboutProductResponseJsonParser.parseSendCommentAboutProductResponse
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendCommentAboutProductFlowViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val dataRepository: DataRepository
) : ViewModel() {

    private val productId = savedState.get<Long>("productId")

    private val sendCommentResultListener = MutableSharedFlow<Boolean>()
    fun observeSendCommentResult() = sendCommentResultListener.asSharedFlow()

    fun sendCommentAboutProduct(rating: Int, comment: String, ) {
        if (productId == null) return

        val userId = dataRepository.fetchUserId() ?: return

        viewModelScope.launch {
            flow { emit(repository.sendCommentAboutProduct(productId, rating, comment, userId)) }
                .catch { debugLog { "send comment error ${it.localizedMessage}" } }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseSendCommentAboutProductResponse()
                    if (response is ResponseEntity.Success) {
                        sendCommentResultListener.emit(true)
                    } else {
                        sendCommentResultListener.emit(false)
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }
}