package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.core.network.messageWithCode
import com.vodovoz.app.domain.general.model.RequestException
import com.vodovoz.app.util.extensions.catchResult
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import retrofit2.Response

 inline fun <T, R> executeRequest(
     crossinline request: suspend () -> Response<T>,
     crossinline mapper: (T) -> R,
     noinline onFail: ((Response<T>) -> Result<R>)? = null,
): Flow<Result<R>> {
    return flow {
        val response = request()

        if (response.isSuccessful && response.body() != null) {
            val result = mapper(response.body()!!)
            emit(Result.success(result))
        } else if (onFail != null) {
            emit(onFail(response))
        } else {
            emit(Result.failure(RequestException(response.messageWithCode())))
        }
    }.catchResult().take(1).onEach { result ->
        result.onFailure { throwable -> debugLog { throwable.stackTraceToString() } }
    }.flowOn(Dispatchers.IO)
}