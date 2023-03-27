package com.vodovoz.app.common.content

import androidx.lifecycle.ViewModel
import com.vodovoz.app.common.content.itemadapter.Item
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class PagingStateViewModel<S : State>(
    idleState: S
) : ViewModel() {

    protected val uiStateListener = MutableStateFlow(PagingState.idle(idleState))
    protected val state
         get() = uiStateListener.value

    fun observeUiState() = uiStateListener.asStateFlow()
}

data class PagingState<S>(
    val data: S,
    val loadingPage: Boolean,
    val isFirstLoad: Boolean,
    val error: ErrorState?,
    val loadMore: Boolean,
    val bottomItem: Item? = null
) {
    companion object {
        fun <S> idle(idleState: S): PagingState<S> {
            return PagingState(
                data = idleState,
                loadingPage = false,
                isFirstLoad = false,
                error = null,
                false
            )
        }
    }
}

sealed class ErrorState {
    abstract val message: String

    data class Error(override val message: String = "Ошибка загрузки. Пропробуйте снова.") : ErrorState()
    data class NetworkError(
        override val message: String = "Проблемы с интернетом. Попробуйте снова."
    ) : ErrorState()

    data class Empty(override val message: String = "Список пуст.") : ErrorState()
}

fun Throwable.toErrorState(): ErrorState {

    return when (this) {
        is UnknownHostException,
        is SocketTimeoutException,
        is ConnectException,
        is SSLException -> ErrorState.NetworkError()
        else -> ErrorState.Error()
    }
}

fun String.stringToErrorState(): ErrorState {
    return ErrorState.Error(this)
}

interface State