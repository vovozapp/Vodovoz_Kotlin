package com.vodovoz.app.feature.productdetail.sendcomment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendCommentAboutProductFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager,
) : ViewModel() {

    private val productId = savedState.get<Long>("productId")

    private val sendCommentResultListener = MutableSharedFlow<Boolean>()
    fun observeSendCommentResult() = sendCommentResultListener.asSharedFlow()

    fun sendCommentAboutProduct(rating: Int, comment: String) {
        if (productId == null) return

        val userId = accountManager.fetchAccountId() ?: return

        viewModelScope.launch {
            flow { emit(repository.sendCommentAboutProduct(productId, rating, comment, userId)) }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        sendCommentResultListener.emit(true)
                    } else {
                        sendCommentResultListener.emit(false)
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch { debugLog { "send comment error ${it.localizedMessage}" } }
                .collect()
        }
    }
}