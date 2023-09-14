package com.vodovoz.app.ui.fragment.send_comment_about_shop

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.comment.SendCommentAboutShopResponseJsonParser.parseSendCommentAboutShopResponse
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendCommentAboutShopFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager,
) : PagingStateViewModel<SendCommentAboutShopFlowViewModel.SendCommentState>(SendCommentState()) {

    fun validate(
        comment: String,
        rating: Int,
    ) {
        uiStateListener.value = state.copy(
            loadingPage = true,
            data = SendCommentState(),
            error = null
        )
        if (rating == 0) {
            uiStateListener.value = state.copy(
                loadingPage = false,
                data = SendCommentState(error = "Поставьте оценку от 1 до 5")
            )
            return
        }

        if (comment.length < FieldValidationsSettings.MIN_COMMENT_LENGTH) {
            uiStateListener.value = state.copy(
                loadingPage = false,
                data = SendCommentState(error = "Длина отзыва должа быть не менее ${FieldValidationsSettings.MIN_COMMENT_LENGTH} символов")
            )
            return
        }

        sendComment(
            comment = comment,
            rating = rating
        )
    }

    private fun sendComment(
        comment: String,
        rating: Int,
    ) {
        viewModelScope.launch {
            flow {
                val userId = accountManager.fetchAccountId()
                emit(
                    repository.sendCommentAboutShop(
                        userId = userId,
                        comment = comment,
                        rating = rating
                    )
                )
            }.flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseSendCommentAboutShopResponse()
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value = state.copy(
                            data = SendCommentState(sendComplete = true),
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
                }.catch {
                    debugLog { "send comments about shop error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    data class SendCommentState(
        val error: String = "",
        val sendComplete: Boolean = false,
    ) : State

}