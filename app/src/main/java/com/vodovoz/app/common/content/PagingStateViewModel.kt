package com.vodovoz.app.common.content

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.vodovoz.app.R
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
    val bottomItem: Item? = null,
    val page: Int? = 1,
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

sealed class ErrorState(
    @DrawableRes
    val iconDrawable: Int = R.drawable.png_logo,
    val message: String,
    val description: String
) {
    data class Error(val messageInfo: String = "Ошибка загрузки.", val desc: String = "Пропробуйте снова.") : ErrorState(message = messageInfo, description = desc)
    data class NetworkError(val messageInfo: String = "Проблемы с интернетом.", val desc: String = "Проверьте соединение с сетью и обновите страницу") : ErrorState(message = messageInfo, iconDrawable = R.drawable.ic_no_connection, description = desc)
    data class Empty(val messageInfo: String = "Список пуст.", val icon: Int = R.drawable.png_logo, val desc: String = "") : ErrorState(message = messageInfo, iconDrawable = icon, description = desc)
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